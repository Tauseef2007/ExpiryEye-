const API_BASE = 'http://localhost:8080/api';
let allItems = [];

document.addEventListener('DOMContentLoaded', () => {
    fetchItems();
    fetchDashboard();

    document.getElementById('item-form').addEventListener('submit', handleFormSubmit);
});

async function fetchItems() {
    showLoading(true);
    try {
        const res = await fetch(`${API_BASE}/items`);
        if (!res.ok) throw new Error('Failed to load items');
        allItems = await res.json();
        applyFilters();
    } catch (err) {
        showNotification(err.message, 'error');
    } finally {
        showLoading(false);
    }
}

async function fetchDashboard() {
    try {
        const res = await fetch(`${API_BASE}/dashboard`);
        if (!res.ok) return;
        const data = await res.json();
        document.getElementById('total-items').innerText = data.totalItems || 0;
        document.getElementById('expiring-soon').innerText = data.expiringSoonItems || 0;
        document.getElementById('expired-items').innerText = data.expiredItems || 0;
        document.getElementById('value-at-risk').innerText = `₹${(data.valueAtRisk || 0).toFixed(2)}`;
    } catch (err) {
        console.error('Dashboard error:', err);
    }
}

function applyFilters() {
    const search = document.getElementById('search-input').value.toLowerCase();
    const category = document.getElementById('category-filter').value;
    const sort = document.getElementById('sort-filter').value;

    let filtered = allItems.filter(item => {
        const matchSearch = item.name.toLowerCase().includes(search) || 
                            (item.notes && item.notes.toLowerCase().includes(search));
        const matchCategory = category === 'ALL' || item.category === category;
        return matchSearch && matchCategory;
    });

    if (sort === 'NEAREST') {
        filtered.sort((a, b) => new Date(a.expiryDate) - new Date(b.expiryDate));
    } else if (sort === 'FARTHEST') {
        filtered.sort((a, b) => new Date(b.expiryDate) - new Date(a.expiryDate));
    } else if (sort === 'NAME') {
        filtered.sort((a, b) => a.name.localeCompare(b.name));
    }

    renderItems(filtered);
}

function renderItems(items) {
    const container = document.getElementById('items-list');
    container.innerHTML = '';

    if (items.length === 0) {
        container.innerHTML = '<p style="color:var(--text-muted); grid-column: 1/-1; text-align:center;">No items found.</p>';
        return;
    }

    items.forEach(item => {
        const card = document.createElement('div');
        card.className = 'item-card';

        let warningHtml = item.safetyWarning ? `<div class="medicine-warning">⚠️ ${item.safetyWarning}</div>` : '';

        card.innerHTML = `
            <div>
                <div style="display:flex; justify-content:space-between; align-items:start; gap:0.5rem;">
                    <h4 style="margin:0; font-size:1rem; word-break:break-word;">${escapeHtml(item.name)}</h4>
                    <span class="badge ${item.status}">${item.status.replace('_', ' ')}</span>
                </div>
                <p style="font-size:0.8rem; color:var(--text-muted); margin:0.3rem 0;">Category: ${item.category}</p>
                <p style="font-size:0.85rem; margin:0.3rem 0;">Expiry: <strong>${item.expiryDate}</strong> (${item.daysRemaining} days remaining)</p>
                ${item.ownerName ? `<p style="font-size:0.8rem; color:var(--text-muted); margin:0.2rem 0;">Owner: ${escapeHtml(item.ownerName)}</p>` : ''}
                ${item.estimatedValue ? `<p style="font-size:0.8rem; color:var(--teal); margin:0.2rem 0;">Value: ₹${item.estimatedValue.toFixed(2)}</p>` : ''}
                ${warningHtml}
            </div>
            <div style="margin-top:1rem; display:flex; gap:0.5rem;">
                <button onclick="editItem(${item.id})" class="btn" style="background:#334155; color:white; padding:0.4rem; font-size:0.8rem;">Edit</button>
                <button onclick="deleteItem(${item.id})" class="btn" style="background:#ef4444; color:white; padding:0.4rem; font-size:0.8rem;">Delete</button>
            </div>
        `;
        container.appendChild(card);
    });
}

async function handleFormSubmit(e) {
    e.preventDefault();
    const id = document.getElementById('item-id').value;
    const payload = {
        name: document.getElementById('name').value,
        category: document.getElementById('category').value,
        expiryDate: document.getElementById('expiryDate').value,
        reminderDays: parseInt(document.getElementById('reminderDays').value),
        ownerName: document.getElementById('ownerName').value || null,
        estimatedValue: parseFloat(document.getElementById('estimatedValue').value) || null,
        notes: document.getElementById('notes').value || null
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_BASE}/items/${id}` : `${API_BASE}/items`;

    try {
        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            const errData = await res.json();
            throw new Error(errData.message || 'Failed to save item');
        }

        showNotification(id ? 'Item updated successfully!' : 'Item added successfully!', 'success');
        resetForm();
        fetchItems();
        fetchDashboard();
    } catch (err) {
        showNotification(err.message, 'error');
    }
}

function editItem(id) {
    const item = allItems.find(i => i.id === id);
    if (!item) return;

    document.getElementById('item-id').value = item.id;
    document.getElementById('name').value = item.name;
    document.getElementById('category').value = item.category;
    document.getElementById('expiryDate').value = item.expiryDate;
    document.getElementById('reminderDays').value = item.reminderDays;
    document.getElementById('ownerName').value = item.ownerName || '';
    document.getElementById('estimatedValue').value = item.estimatedValue || '';
    document.getElementById('notes').value = item.notes || '';

    document.getElementById('form-title').innerText = 'Edit Item';
    document.getElementById('save-btn').innerText = 'Update Item';
    document.getElementById('cancel-btn').classList.remove('hidden');
}

async function deleteItem(id) {
    if (!confirm('Are you sure you want to delete this item?')) return;

    try {
        const res = await fetch(`${API_BASE}/items/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Failed to delete item');
        showNotification('Item deleted', 'success');
        fetchItems();
        fetchDashboard();
    } catch (err) {
        showNotification(err.message, 'error');
    }
}

function resetForm() {
    document.getElementById('item-form').reset();
    document.getElementById('item-id').value = '';
    document.getElementById('form-title').innerText = 'Add New Item';
    document.getElementById('save-btn').innerText = 'Save Item';
    document.getElementById('cancel-btn').classList.add('hidden');
}

function showNotification(msg, type) {
    const el = document.getElementById('notification');
    el.innerText = msg;
    el.className = `notification ${type}`;
    setTimeout(() => { el.className = 'notification hidden'; }, 4000);
}

function showLoading(show) {
    document.getElementById('loading').className = show ? 'loading-state' : 'loading-state hidden';
}

function escapeHtml(str) {
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}
