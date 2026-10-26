// Base API URL for Backend Spring Boot Server
const API_BASE_URL = 'http://localhost:8080/api';

// Global state variables
let currentUser = JSON.parse(localStorage.getItem('expiry_user')) || null;
let allItems = [];
let editingItemId = null;

// DOM Loaded Event Listener
document.addEventListener('DOMContentLoaded', () => {
    checkAuthState();
    setupEventListeners();
});

// Setup all user interaction event listeners
function setupEventListeners() {
    // Auth Toggles
    const showSignup = document.getElementById('show-signup');
    const showLogin = document.getElementById('show-login');
    if (showSignup) showSignup.addEventListener('click', toggleAuthMode);
    if (showLogin) showLogin.addEventListener('click', toggleAuthMode);

    // Forms Submission
    const loginForm = document.getElementById('login-form');
    const signupForm = document.getElementById('signup-form');
    if (loginForm) loginForm.addEventListener('submit', handleLogin);
    if (signupForm) signupForm.addEventListener('submit', handleSignup);

    // Logout Button
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) logoutBtn.addEventListener('click', handleLogout);

    // Modal Control Buttons
    const addItemBtn = document.getElementById('add-item-btn');
    const closeModalBtn = document.getElementById('close-modal');
    const closeModalX = document.getElementById('close-modal-x');
    const itemModal = document.getElementById('item-modal');
    
    if (addItemBtn) addItemBtn.addEventListener('click', () => itemModal.style.display = 'flex');
    if (closeModalBtn) closeModalBtn.addEventListener('click', () => itemModal.style.display = 'none');
    if (closeModalX) closeModalX.addEventListener('click', () => itemModal.style.display = 'none');

    // Add Item Form Submit
    const addItemForm = document.getElementById('add-item-form');
    if (addItemForm) addItemForm.addEventListener('submit', handleAddItem);

    // Search, Filter & Sort Event Listeners
    const searchBar = document.getElementById('search-bar');
    const filterCategory = document.getElementById('filter-category');
    const sortBy = document.getElementById('sort-by');

    if (searchBar) searchBar.addEventListener('input', renderItems);
    if (filterCategory) filterCategory.addEventListener('change', renderItems);
    if (sortBy) sortBy.addEventListener('change', renderItems);
}

// Toggle between Login and Signup forms
function toggleAuthMode(e) {
    e.preventDefault();
    const loginForm = document.getElementById('login-form');
    const signupForm = document.getElementById('signup-form');
    const subtitle = document.getElementById('auth-subtitle');

    if (loginForm.style.display === 'none') {
        loginForm.style.display = 'block';
        signupForm.style.display = 'none';
        subtitle.textContent = 'Manage your product expiries seamlessly';
    } else {
        loginForm.style.display = 'none';
        signupForm.style.display = 'block';
        subtitle.textContent = 'Create an account to get started';
    }
}

// Check if user is logged in
function checkAuthState() {
    const authView = document.getElementById('auth-view');
    const dashboardView = document.getElementById('dashboard-view');
    const welcomeMsg = document.getElementById('welcome-msg');

    if (currentUser) {
        authView.style.display = 'none';
        dashboardView.style.display = 'block';
        welcomeMsg.textContent = `Welcome, ${currentUser.name}`;
        fetchUserItems();
    } else {
        authView.style.display = 'flex';
        dashboardView.style.display = 'none';
    }
}

// Handle User Signup
async function handleSignup(e) {
    e.preventDefault();
    const name = document.getElementById('signup-name').value;
    const email = document.getElementById('signup-email').value;
    const password = document.getElementById('signup-password').value;

    try {
        const response = await fetch(`${API_BASE_URL}/users/signup`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });

        if (response.ok) {
            alert('Signup Successful! Please login.');
            document.getElementById('signup-form').reset();
            document.getElementById('show-login').click();
        } else {
            const errText = await response.text();
            alert('Signup failed: ' + errText);
        }
    } catch (err) {
        console.error('Error during signup:', err);
        alert('Server connection error!');
    }
}

// Handle User Login
async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${API_BASE_URL}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            const user = await response.json();
            currentUser = user;
            localStorage.setItem('expiry_user', JSON.stringify(user));
            checkAuthState();
        } else {
            alert('Invalid email or password!');
        }
    } catch (err) {
        console.error('Error during login:', err);
        alert('Server connection error!');
    }
}

// Handle Logout
function handleLogout() {
    currentUser = null;
    localStorage.removeItem('expiry_user');
    checkAuthState();
}

// Fetch items for logged-in user from Backend
async function fetchUserItems() {
    if (!currentUser) return;

    try {
        const response = await fetch(`${API_BASE_URL}/items/user/${currentUser.id}`);
        if (response.ok) {
            allItems = await response.json();
            renderItems();
        }
    } catch (err) {
        console.error('Error fetching items:', err);
    }
}

// Quick Date helper in modal
function setQuickDate(days) {
    const dateInput = document.getElementById('item-expiry');
    const targetDate = new Date();
    targetDate.setDate(targetDate.getDate() + days);
    dateInput.value = targetDate.toISOString().split('T')[0];
}


async function handleAddItem(e) {
    e.preventDefault();

    const itemName = document.getElementById('item-name').value.trim();

    if (!itemName) {
        alert('Please enter an item name.');
        return;
    }

    const newItem = {
        name: itemName,
        ownerName: currentUser ? currentUser.name : '',
        category: document.getElementById('item-category').value,
        expiryDate: document.getElementById('item-expiry').value,
        quantity: parseInt(document.getElementById('item-quantity').value) || 1,
        estimatedValue: parseFloat(document.getElementById('item-value').value) || 0,
        notes: document.getElementById('item-notes').value,
        user: { id: currentUser ? currentUser.id : 1 }
    };

    const isEditing = editingItemId !== null;
    const url = isEditing
        ? `${API_BASE_URL}/items/${editingItemId}`
        : `${API_BASE_URL}/items`;

    try {
        const response = await fetch(url, {
            method: isEditing ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newItem)
        });

        if (response.ok) {
            document.getElementById('add-item-form').reset();
            document.getElementById('item-modal').style.display = 'none';

            editingItemId = null;

            fetchUserItems();

            alert(isEditing
                ? 'Item updated successfully!'
                : 'Item added successfully!');
        } else {
            const errText = await response.text();
            alert(
                (isEditing ? 'Failed to update item: ' : 'Failed to add item: ')
                + errText
            );
        }
    } catch (err) {
        console.error('Error saving item:', err);
        alert('Server connection error!');
    }
}


// Delete Item by ID
async function deleteItem(id) {
    if (!confirm('Are you sure you want to delete this item?')) return;

    try {
        const response = await fetch(`${API_BASE_URL}/items/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            fetchUserItems();
        } else {
            alert('Failed to delete item.');
        }
    } catch (err) {
        console.error('Error deleting item:', err);
    }
}


function editItem(id) {
    const item = allItems.find(item => String(item.id) === String(id));

    if (!item) {
        alert('Item not found!');
        return;
    }
    editingItemId = id;

    document.getElementById('item-name').value = item.name || '';
    document.getElementById('item-category').value = item.category || '';
    document.getElementById('item-expiry').value = item.expiryDate || '';
    document.getElementById('item-quantity').value = item.quantity || 1;
    document.getElementById('item-value').value = item.estimatedValue || 0;
    document.getElementById('item-notes').value = item.notes || '';

    document.getElementById('item-modal').style.display = 'flex';
}


// Render Items as Modern Cards with Search, Filter & Sort
function renderItems() {
    const gridContainer = document.getElementById('items-grid');
    const searchQuery = document.getElementById('search-bar').value.toLowerCase();
    const selectedCategory = document.getElementById('filter-category').value;
    const sortBy = document.getElementById('sort-by').value;

    gridContainer.innerHTML = '';

    // 1. Filter items based on Search query and Category
    let filtered = allItems.filter(item => {
        const matchesSearch = item.name.toLowerCase().includes(searchQuery);
        const matchesCategory = (selectedCategory === 'ALL' || item.category === selectedCategory);
        return matchesSearch && matchesCategory;
    });

    // 2. Sort items based on dropdown selection
    filtered.sort((a, b) => {
        if (sortBy === 'expiry-asc') {
            return new Date(a.expiryDate) - new Date(b.expiryDate);
        } else if (sortBy === 'expiry-desc') {
            return new Date(b.expiryDate) - new Date(a.expiryDate);
        } else if (sortBy === 'name-asc') {
            return a.name.localeCompare(b.name);
        }
    });

    // 3. Handle empty state
    if (filtered.length === 0) {
        gridContainer.innerHTML = `<p style="grid-column: 1/-1; text-align: center; color: #94a3b8; padding: 40px;">No items found.</p>`;
        return;
    }

    // 4. Generate Cards Dynamically
    const today = new Date().toISOString().split('T')[0];

    filtered.forEach(item => {
        const isExpired = item.expiryDate < today;
        const statusClass = isExpired ? 'status-expired' : 'status-active';
        const statusText = isExpired ? 'EXPIRED' : 'ACTIVE';

        const card = document.createElement('div');
        card.className = 'item-card';
        card.innerHTML = `
            <div>
                <div class="card-header">
                    <span class="card-title">${item.name}</span>
                    <span class="card-category-badge">${item.category.replace(/_/g, ' ')}</span>
                </div>
                <div class="card-body">
                    <p><b>Expiry:</b> ${item.expiryDate}</p>
                    <p><b>Qty:</b> ${item.quantity || 1}</p>
                    <p><b>Status:</b> <span class="status-badge ${statusClass}">${statusText}</span></p>
                    ${item.notes ? `<p style="font-size: 13px; color: #94a3b8; margin-top: 4px;"><i>Notes: ${item.notes}</i></p>` : ''}
                </div>
            </div>
            
            <div class="card-footer">
            <span class="card-value">₹${item.estimatedValue || 0}</span>
            <div class="item-actions">
            <button class="btn-edit" onclick="editItem(${item.id})">
            Edit
            </button>
            <button class="btn-danger-sm" onclick="deleteItem(${item.id})">
            Delete
            </button>
            </div>
            </div>

        `;
        gridContainer.appendChild(card);
    });
}