# 👋 Hey there, I'm **Yaroslav Novak**

🎓 Student at **Wrocław University of Science and Technology**  
📚 Field of study: *Informatyczne Systemy Automatyki* (Information Systems of Automation)  
💻 **Java Developer** specialized in SaaS platforms, Telegram automation, and backend architecture  
🏆 **Competitive programmer** with a strong algorithmic and academic background  
⚡ Passionate about building intelligent, scalable, and self-configurable systems

---

## 🚀 About Me

- 🧩 Over 8 months of **hands-on Java experience**
- 💡 Deep understanding of **algorithms**, **data structures**, and **clean system architecture**
- 🥇 Multi-year **Informatics Olympiad competitor**, consistently ranked top-tier at:
    - School, city, and regional levels (2nd place multiple times)
    - Represented **Khmelnytskyi region** at the **Ukrainian national stage**
- 🧠 Strong problem-solving mindset — applying Olympiad-level logic to enterprise-grade systems
- 🔨 Creator of **SalesUp**, a modular SaaS platform for building customizable Telegram sales bots
- ⚙️ Author of **SpotLab**, a distributed architecture core for managing multiple bots
- 🌦️ Built **GlobalWeatherApp**, a desktop JavaFX tool that displays live weather data from any city in the world

---

## 🧰 Tech Stack

| Category | Technologies |
|-----------|--------------|
| **Languages** | Java 17+, C++, SQL, Python |
| **Frameworks / Libraries** | Spring Boot, Hibernate, Lombok, TelegramBots API |
| **Databases** | PostgreSQL, Redis |
| **Caching** | Caffeine Cache, Redis Cache |
| **Messaging / Integration** | RabbitMQ, Kafka |
| **Architecture & Design** | Clean Architecture, Domain-Driven Design, Event-Driven Systems |
| **Tools & Environment** | Maven, Git, IntelliJ IDEA |
| **Other** | REST API design, JSON configuration, JavaFX / Swing |

---

## 💼 Featured Projects

### 🤖 [SalesUp](https://t.me/TelSalesBot_bot)
> **SalesUp — A Java-powered SaaS platform that lets anyone build, customize, and deploy Telegram sales bots with no code.**

---

### 🚀 Overview

**SalesUp** is a next-generation **SaaS platform** written in **Java + Spring Boot**, designed to help businesses and individuals **create, configure, and manage their own Telegram sales bots** — entirely through chat.

It’s a **multi-bot ecosystem**, where one central bot acts as a **Configurator** that can spawn and manage multiple **autonomous seller bots**, each with its own logic, catalog, and customers.

SalesUp was built from scratch with **clean architecture**, **caching optimization**, and **cross-bot synchronization** — resulting in a platform that’s fast, stable, and enterprise-ready.

---

### 🧠 Key Features

- 🏗 **Modular architecture** (Bot → Dispatcher → Handlers → Services → Repository)
- ⚙️ **Configurator Bot** — allows users to:
    - Create and deploy their own Telegram seller bots
    - Edit buttons, messages, photos, and videos
    - Add/remove products, update prices and descriptions
    - Manage customers and order history
    - Apply **referral discounts** automatically
- 🤖 **Seller Bots** — fully functional Telegram shops auto-generated from Configurator settings
    - Product catalogs, media, multilingual support
    - Autonomous, scalable, and connected to a shared backend
- 🔄 **Real-time data sync** via **RabbitMQ** message broker
- ⚡ **Caffeine + Redis caching** for ultra-fast response
- 🧩 **Dynamic JSON configuration** stored in PostgreSQL
- 💬 **Multi-tenant system** — one Configurator, many bots, isolated per user

---

### 🧠 Deep Dive: The Configurator Bot

The **Configurator Bot** is the **core of SalesUp** — a Telegram-based admin interface for non-technical users.  
It allows complete control over every aspect of their bots:

#### 🔧 Capabilities
- Rename buttons, edit text, or attach images and videos
- Add submenus, custom actions, and media buttons
- Schedule updates or expiry dates for media
- Create new menu levels and link them dynamically
- Instantly apply changes — no redeploy required

All modifications are stored as **structured JSON** and sent through **RabbitMQ** to all relevant seller bots.  
Each bot automatically invalidates its cache to load updated data in real time.

#### 🏗 Internal Flow
