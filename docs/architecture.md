# ExpiryEye — Architecture Diagram

```mermaid
flowchart TB
    subgraph Browser["Browser"]
        HTML["index.html / payment.html"]
        CSS["style.css"]
        JS["app.js: DOM events, validation, search/filter/sort, fetch"]
        HTML --> CSS
        HTML --> JS
    end
    JS -->|"HTTP requests / JSON"| API["Spring Boot REST API :8080"]
    subgraph Backend["Java backend"]
        API --> CTRL["Controllers"]
        CTRL --> SVC["Services"]
        CTRL --> REPO["Spring Data JPA repositories"]
        SVC --> REPO
        SVC --> EXP["Expiry calculation and reminder scheduler"]
    end
    REPO --> DB[("MySQL: expiryeye_db")]
    EXP --> SMTP["Gmail SMTP"]
    CTRL --> RZP["Razorpay test API"]
```

Browser → JavaScript → REST API → Java controllers/services → JPA repositories → MySQL.

External integrations: scheduled expiry email reminders use Gmail SMTP; eligible payment/renewal flows use Razorpay test mode.
