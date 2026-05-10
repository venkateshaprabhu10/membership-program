# FirstClub Membership Program

A backend system for **FirstClub** — a quality-first quick commerce platform delivering fresh fruits, groceries, and daily essentials across Bengaluru. This project implements a configurable, extensible membership program with tiered benefits, cohort-based perks, and automatic tier progression.

---

## Personal Note

*This project is built with the help of Claude Code. I spent time thinking about the use case and designed the LLD, trying to make it as generic and configurable as possible without increasing the complexity.*

---

## Table of Contents

1. [Use Cases Covered](#use-cases-covered)
2. [System Architecture](#system-architecture)
3. [Tech Stack](#tech-stack)
4. [Project Structure](#project-structure)
5. [Getting Started](#getting-started)
6. [H2 Database Console](#h2-database-console)
7. [API Reference](#api-reference)
   - [Health & Status](#health--status)
   - [Members](#members)
   - [Plans](#plans)
   - [Tiers](#tiers)
   - [Subscriptions](#subscriptions)
   - [Orders](#orders)
   - [Admin — Tiers & Perks](#admin--tiers--perks)
   - [Admin — Cohorts](#admin--cohorts)
   - [Admin — Sub-brands](#admin--sub-brands)
8. [Configuring for a New Brand](#configuring-for-a-new-brand)
9. [Best Practices Followed](#best-practices-followed)
10. [Design & Concurrency Notes](#design--concurrency-notes)

---

## Use Cases Covered

### Core (from problem statement)

| # | Use Case |
|---|---|
| 1 | Browse all available membership plans (Monthly, Quarterly, Yearly — configurable duration & pricing) |
| 2 | Browse all tiers (Silver, Gold, Platinum) with their attached perks |
| 3 | Subscribe a member to a plan + starting tier |
| 4 | Manually upgrade membership tier |
| 5 | Manually downgrade membership tier |
| 6 | Cancel an active subscription |
| 7 | View current subscription details and expiry date |
| 8 | Automatic tier upgrade based on **order count** (e.g. 5+ orders in 30 days → Gold) |
| 9 | Automatic tier upgrade based on **total order value** (e.g. ₹5,000+ in 30 days → Gold) |
| 10 | Automatic tier qualification via **cohort membership** (e.g. being in "VIP" cohort → Platinum) |
| 11 | **Free delivery** perk — configurable min-order threshold per tier (Silver: above ₹299, Gold/Platinum: always free) |
| 12 | **Item/category discount** perk — percentage off on selected categories (fruits, dairy, vegetables, or all products) |
| 13 | **Exclusive early access** perk — N-hour advance access to new products and sales |
| 14 | **Priority support** perk — dedicated support channel for premium members |
| 15 | Tier criteria are composable — multiple criteria per tier evaluated with OR logic |

### Extra Use Cases We Solved

| # | Use Case |
|---|---|
| 16 | Register members (with optional sub-brand affiliation) |
| 17 | Record orders against members (feeds into tier evaluation engine) |
| 18 | View order history per member |
| 19 | **Cohort management** — create named cohorts, assign members to cohorts |
| 20 | **Cohort-specific bonus perks** — perks that stack on top of tier perks (e.g. "Early Adopter Bonus" 3% flat discount) |
| 21 | **Perk stacking strategies** — two configurable strategies: `UNION_MAX_VALUE` (best perk per type wins) or `ADDITIVE` (values sum up); switched via a single config line |
| 22 | **Effective perks API** — single endpoint returns the fully merged perk set for a member (tier + all cohorts combined) |
| 23 | **Sub-brands** — lightweight product lines within a brand (e.g. FirstClub Fresh, FirstClub B2B) |
| 24 | Sub-brand as cohort — B2B users automatically get B2B-specific perks on top of their tier |
| 25 | **Admin: create new perk definitions** at runtime — no code change needed |
| 26 | **Admin: attach/enable perks to tiers** at runtime |
| 27 | **Admin: attach/enable perks to cohorts** at runtime |
| 28 | **Admin: create new sub-brands** |
| 29 | **Admin: create new cohorts** and add members to them |
| 30 | **Async event-driven tier evaluation** — order recording returns immediately; tier re-evaluation happens in a background thread |
| 31 | **Optimistic locking** on subscriptions — concurrent tier updates are safe; second concurrent write is detected and discarded |
| 32 | **Deploy-per-brand configurability** — the entire brand identity (name, currency, perk stacking strategy) is set in `application.properties`; switching brands requires zero code change |
| 33 | Seeded demo data for FirstCl<br/>ub (plans, tiers, perks, cohorts, sample members) loaded automatically on startup |

---

## System Architecture

```
HTTP Request
    │
    ▼
Controller Layer          (REST endpoints, input validation)
    │
    ▼
Service Layer             (business logic, transaction boundaries)
    │              │
    ▼              ▼
Repository Layer   ApplicationEventPublisher
(Spring Data JPA)       │
    │              ▼
    │     [Background Thread — tier-eval-*]
    │              │
    ▼              ▼
H2 In-Memory DB   TierEvaluationService
                  (Strategy: OrderCount | OrderValue | CohortMembership)
                        │
                        ▼
                  PerkResolutionService
                  (Strategy: UnionMaxValue | Additive)
```

**Key patterns used:**

| Pattern | Where |
|---|---|
| Strategy | Tier criteria evaluation (`OrderCount`, `OrderValue`, `CohortMembership`) |
| Strategy | Perk stacking (`UnionMaxValue`, `Additive`) — selected at startup via config |
| Observer / Event | `OrderRecordedEvent` → async `TierEvaluationListener` |
| Builder (fluent helper) | `DataSeeder` helper methods |
| Factory | `PerkStrategyConfig` selects the active `PerkStackingStrategy` bean |

---

## Tech Stack

| Component | Choice |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Persistence | Spring Data JPA + Hibernate |
| Database | H2 (in-memory, zero setup) |
| Build tool | Gradle (wrapper included) |
| Boilerplate reduction | Lombok |
| JSON | Jackson (auto-configured) |

---

## Project Structure

```
src/main/java/com/venkateshaprabhu/membership/
├── config/               BrandConfig, AsyncConfig, PerkStrategyConfig
├── entity/               JPA entities (Member, Subscription, MembershipTier, ...)
├── enums/                CriteriaType, PerkType, PerkSource, SubscriptionStatus, ...
├── repository/           Spring Data JPA repositories
├── strategy/
│   ├── tier/             TierCriteriaStrategy + 3 implementations
│   └── perk/             PerkStackingStrategy + 2 implementations + SourcedPerk
├── event/                OrderRecordedEvent, TierChangedEvent
│   TierEvaluationListener, TierChangeAuditListener
├── service/              Business logic (8 services)
├── controller/           REST controllers + admin controllers
├── dto/
│   ├── request/          Input records (validated)
│   └── response/         Output records
├── exception/            GlobalExceptionHandler, typed exceptions
└── DataSeeder.java       Seeds FirstClub data on startup
```

---

## Getting Started

### Prerequisites

- **Java 21** — verify with `java -version`
- No database installation needed (H2 is embedded)
- No other services needed

### Run from source (recommended)

```bash
# Clone or unzip the project, then:
cd membership-program

# macOS / Linux
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

The application starts on **`http://localhost:8080`**.

On startup you will see:
```
FirstClub Membership System initialized: 3 plans, 3 tiers, 9 perks seeded.
```

### Build a runnable JAR

```bash
./gradlew build -x test
java -jar build/libs/membership-program-0.0.1-SNAPSHOT.jar
```

### Run as a different brand (e.g. Licious)

Create `src/main/resources/application-licious.properties`:
```properties
brand.name=Licious
brand.slug=licious
brand.currency=INR
brand.perk-stacking-strategy=ADDITIVE
```

Then run:
```bash
./gradlew bootRun --args='--spring.profiles.active=licious'
```

---

## H2 Database Console

While the app is running, open your browser and go to:

```
http://localhost:8080/h2-console
```

Use these connection details:

| Field | Value |
|---|---|
| Driver Class | `org.h2.Driver` |
| JDBC URL | `jdbc:h2:mem:firstclubdb` |
| User Name | `sa` |
| Password | *(leave blank)* |

Useful queries:

```sql
-- All members
SELECT * FROM MEMBERS;

-- Subscriptions with plan and tier names
SELECT s.id, m.name AS member, p.name AS plan, t.name AS tier, s.status, s.expiry_date
FROM SUBSCRIPTIONS s
JOIN MEMBERS m ON s.member_id = m.id
JOIN MEMBERSHIP_PLANS p ON s.plan_id = p.id
JOIN MEMBERSHIP_TIERS t ON s.tier_id = t.id;

-- Perks per tier
SELECT t.name AS tier, pd.name AS perk, pd.type, pd.numeric_value
FROM TIER_PERKS tp
JOIN MEMBERSHIP_TIERS t ON tp.tier_id = t.id
JOIN PERK_DEFINITIONS pd ON tp.perk_id = pd.id;

-- Cohort memberships
SELECT m.name AS member, c.name AS cohort
FROM USER_COHORTS uc
JOIN MEMBERS m ON uc.member_id = m.id
JOIN COHORTS c ON uc.cohort_id = c.id;

-- All orders
SELECT * FROM ORDER_RECORDS ORDER BY ordered_at DESC;
```

---

## API Reference

All responses follow this envelope:
```json
{ "success": true, "message": "...", "data": { ... } }
```

Seeded IDs for reference:
- **Members**: 1 = Priya Sharma, 2 = Rahul Mehta
- **Plans**: 1 = Fresh Monthly (₹149/30d), 2 = Fresh Quarterly (₹399/90d), 3 = Club Annual (₹999/365d)
- **Tiers**: 1 = Silver, 2 = Gold, 3 = Platinum
- **Cohorts**: 1 = Early Adopters, 2 = B2B Buyers
- **Sub-brands**: 1 = FirstClub Fresh (slug: `fresh`), 2 = FirstClub B2B (slug: `b2b`)

---

### Health & Status

Two lightweight endpoints to verify the service is up — useful when sharing or demoing on another machine.

#### Ping (simplest possible check)
```bash
curl http://localhost:8080/ping
# → pong
```

#### Health (brand info + status)
```bash
curl http://localhost:8080/api/health
```
```json
{
  "success": true,
  "message": "Service is running",
  "data": {
    "status": "UP",
    "brand": "FirstClub",
    "currency": "INR",
    "perkStrategy": "UNION_MAX_VALUE"
  }
}
```

---

### Members

#### Create a member
```bash
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ananya Iyer",
    "email": "ananya@example.com",
    "phone": "9876543212",
    "subBrandSlug": "fresh"
  }'
```

#### Get a member
```bash
curl http://localhost:8080/api/members/1
```

---

### Plans

#### List all active plans
```bash
curl http://localhost:8080/api/plans
```

Sample response:
```json
{
  "success": true,
  "data": [
    { "id": 1, "name": "Fresh Monthly", "durationDays": 30, "price": 149.00, "currency": "INR" },
    { "id": 2, "name": "Fresh Quarterly", "durationDays": 90, "price": 399.00, "currency": "INR" },
    { "id": 3, "name": "Club Annual", "durationDays": 365, "price": 999.00, "currency": "INR" }
  ]
}
```

---

### Tiers

#### List all active tiers with their perks
```bash
curl http://localhost:8080/api/tiers
```

---

### Subscriptions

#### Subscribe a member to a plan + tier
```bash
curl -X POST http://localhost:8080/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{
    "memberId": 1,
    "planId": 3,
    "tierId": 1
  }'
```

#### Get current subscription
```bash
curl http://localhost:8080/api/subscriptions/1
```

#### Manually upgrade or downgrade tier
```bash
# Upgrade Priya to Gold (tierId: 2)
curl -X PUT http://localhost:8080/api/subscriptions/1/tier \
  -H "Content-Type: application/json" \
  -d '{ "tierId": 2 }'

# Downgrade back to Silver (tierId: 1)
curl -X PUT http://localhost:8080/api/subscriptions/1/tier \
  -H "Content-Type: application/json" \
  -d '{ "tierId": 1 }'
```

#### Cancel subscription
```bash
curl -X DELETE http://localhost:8080/api/subscriptions/1
```

#### Get effective perks (tier perks + all cohort perks, merged by stacking strategy)
```bash
curl http://localhost:8080/api/subscriptions/1/perks
```

Sample response (Priya at Gold tier, in Early Adopters cohort):
```json
{
  "data": {
    "memberId": 1,
    "memberName": "Priya Sharma",
    "tierName": "Gold",
    "effectivePerks": [
      { "name": "Free Delivery Premium", "type": "FREE_DELIVERY", "numericValue": 0.0, "source": "TIER" },
      { "name": "Fruits & Dairy Discount 10%", "type": "ITEM_DISCOUNT", "numericValue": 10.0, "source": "TIER" },
      { "name": "Exclusive Early Access", "type": "EXCLUSIVE_ACCESS", "numericValue": 24.0, "source": "TIER" },
      { "name": "Early Adopter Bonus", "type": "FLAT_DISCOUNT", "numericValue": 3.0, "source": "COHORT" }
    ]
  }
}
```

---

### Orders

#### Record an order (triggers async tier evaluation)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "memberId": 1,
    "externalOrderId": "FC-2026-001",
    "amount": 1250,
    "subBrandSlug": "fresh"
  }'
```

> The response returns immediately. Tier re-evaluation happens asynchronously in the background thread (`tier-eval-*`). Check the subscription after a moment to see the updated tier.

#### Watch automatic tier progression

```bash
# Subscribe at Silver
curl -X POST http://localhost:8080/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{"memberId": 1, "planId": 1, "tierId": 1}'

# Record 5 orders (Gold threshold: 5 orders in 30 days)
for i in 1 2 3 4 5; do
  curl -X POST http://localhost:8080/api/orders \
    -H "Content-Type: application/json" \
    -d "{\"memberId\": 1, \"externalOrderId\": \"FC-ORD-$i\", \"amount\": 800}"
done

sleep 2  # wait for async evaluation

# Check tier — should now be Gold
curl http://localhost:8080/api/subscriptions/1
```

#### View order history for a member
```bash
curl http://localhost:8080/api/orders/member/1
```

---

### Admin — Tiers & Perks

#### Create a new perk definition
```bash
curl -X POST http://localhost:8080/api/admin/tiers/perks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Weekend Flash Discount 20%",
    "description": "20% off every weekend",
    "type": "ITEM_DISCOUNT",
    "numericValue": 20,
    "configJson": "{\"percentage\": 20, \"days\": [\"SATURDAY\", \"SUNDAY\"]}"
  }'
```

**PerkType values:** `FREE_DELIVERY`, `ITEM_DISCOUNT`, `EXCLUSIVE_ACCESS`, `PRIORITY_SUPPORT`, `FLAT_DISCOUNT`

#### Attach an existing perk to a tier
```bash
# Attach perk id=5 (All Products 15% discount) to Platinum tier (id=3)
curl -X POST http://localhost:8080/api/admin/tiers/3/perks \
  -H "Content-Type: application/json" \
  -d '{ "perkId": 5, "enabled": true }'
```

---

### Admin — Cohorts

#### Create a new cohort
```bash
curl -X POST http://localhost:8080/api/admin/cohorts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Weekend Warriors",
    "description": "Members who order primarily on weekends",
    "subBrandId": null
  }'
```

#### Add a member to a cohort
```bash
# Add Priya (memberId: 1) to Early Adopters cohort (cohortId: 1)
curl -X POST http://localhost:8080/api/admin/cohorts/1/members \
  -H "Content-Type: application/json" \
  -d '{ "memberId": 1 }'
```

#### Attach a perk to a cohort
```bash
# Attach perk id=8 (Early Adopter Bonus) to cohort id=1
curl -X POST http://localhost:8080/api/admin/cohorts/1/perks \
  -H "Content-Type: application/json" \
  -d '{ "perkId": 8, "enabled": true }'
```

#### List members of a cohort
```bash
curl http://localhost:8080/api/admin/cohorts/1/members
```

---

### Admin — Sub-brands

#### Create a new sub-brand
```bash
curl -X POST http://localhost:8080/api/admin/sub-brands \
  -H "Content-Type: application/json" \
  -d '{
    "name": "FirstClub Organics",
    "slug": "organics",
    "description": "Certified organic produce line"
  }'
```

---

## Configuring for a New Brand

Everything brand-specific lives in `application.properties`. No code change is needed.

```properties
# Brand identity
brand.name=FirstClub
brand.slug=firstclub
brand.currency=INR

# Perk stacking: UNION_MAX_VALUE (best perk wins) or ADDITIVE (values sum up)
brand.perk-stacking-strategy=UNION_MAX_VALUE
```

To onboard a new brand (e.g. Licious), create `application-licious.properties` with Licious-specific values and run with `--spring.profiles.active=licious`. Plans, tiers, perks, and cohorts are seeded via `DataSeeder` — update that class (or replace it with a DB migration) to seed the new brand's data.

---

## Best Practices Followed

### Architecture & Design Patterns

| Practice | How it's applied |
|---|---|
| **Layered architecture** | Strict Controller → Service → Repository separation; no layer skips |
| **Strategy pattern** | `TierCriteriaStrategy` (3 impls) and `PerkStackingStrategy` (2 impls) — new strategies added without touching existing code |
| **Observer / Event pattern** | `OrderService` publishes `OrderRecordedEvent`; `TierEvaluationListener` reacts — zero coupling between order and tier logic |
| **Factory pattern** | `PerkStrategyConfig` selects the active `PerkStackingStrategy` bean at startup based on brand config |
| **Deploy-per-brand (12-Factor)** | Brand identity and behaviour live entirely in `application.properties`; no tenant ID in code or headers |
| **EnumMap for strategy dispatch** | Strategies are registered in an `EnumMap<CriteriaType, TierCriteriaStrategy>` at construction — O(1) dispatch, no if-else chains |

### SOLID Principles

| Principle | Where |
|---|---|
| **Single Responsibility** | Each service owns exactly one domain (`OrderService`, `TierEvaluationService`, `PerkResolutionService`, etc.) |
| **Open / Closed** | Adding a new tier criterion or perk strategy = new class + new enum value; zero changes to existing classes |
| **Liskov Substitution** | Any `TierCriteriaStrategy` or `PerkStackingStrategy` impl is interchangeable at the injection point |
| **Interface Segregation** | Strategy interfaces are thin — single `evaluate()` or `merge()` method each |
| **Dependency Inversion** | Services depend on interfaces (`TierCriteriaStrategy`, `PerkStackingStrategy`), not concrete classes |

### Java & Spring Best Practices

| Practice | How it's applied |
|---|---|
| **Constructor injection** | All dependencies injected via constructor (`@RequiredArgsConstructor`), never field injection |
| **Immutable DTOs** | Request and response DTOs are Java records — immutable by construction |
| **Fail-fast validation** | `@Valid` + Bean Validation annotations on all request DTOs; rejected at the controller boundary |
| **Transactional hygiene** | `@Transactional` on writes, `@Transactional(readOnly = true)` on reads — prevents accidental dirty writes on read paths |
| **Lazy loading** | `FetchType.LAZY` on all entity relationships — no N+1 surprise loads |
| **Optimistic locking** | `@Version` on `Subscription` — concurrent tier updates are safe without pessimistic locks |
| **Async with named pool** | `@Async("tierEvaluationExecutor")` — burst of orders cannot starve main HTTP threads; threads are named `tier-eval-*` for easy monitoring |
| **Global exception handling** | `@RestControllerAdvice` maps typed exceptions to correct HTTP status codes consistently |
| **Consistent API envelope** | All responses use `ApiResponse<T>` — predictable shape for every caller |
| **Semantic HTTP status codes** | `201` for creates, `200` for reads/updates, `400` for business rule violations, `404` for not found, `500` for unexpected errors |

### Code Quality

| Practice | How it's applied |
|---|---|
| **Minimal, meaningful comments** | Comments only where the WHY is non-obvious (concurrency strategy, FREE_DELIVERY inversion, transient aggregation) |
| **Descriptive naming** | No abbreviations; class/method names read like sentences (`evaluateAndUpdate`, `getEffectivePerks`, `anyCriterionPasses`) |
| **No over-engineering** | No unnecessary abstractions, no premature generics, no half-finished features |
| **Configurable seed data** | `DataSeeder` sets up a complete runnable demo on startup — no manual DB setup needed |

---

## Design & Concurrency Notes

### Tier evaluation is event-driven and async

When an order is recorded, `OrderService` publishes an `OrderRecordedEvent`. A separate thread pool (`tier-eval-*`, 2–5 threads) picks this up via `TierEvaluationListener`. The HTTP response returns immediately — the tier update happens in the background. This decouples order recording from tier logic and keeps the order API fast.

### Concurrent tier updates are safe

`Subscription` carries a `@Version` field (JPA optimistic locking). If two orders arrive simultaneously and both trigger tier evaluation, both threads race to update the subscription. The loser gets `ObjectOptimisticLockingFailureException`, which is caught and logged — the winner already wrote the correct tier, so no retry is needed and no data is lost.

### Perk stacking is a pluggable strategy

Both `UnionMaxValuePerkStrategy` and `AdditivePerkStrategy` are Spring beans. `PerkStrategyConfig` picks one at startup based on `brand.perk-stacking-strategy`. Adding a third strategy (e.g. `PriorityOrderPerkStrategy`) requires only a new `@Component` class and a new enum value — no existing code changes.

### Tier criteria are also pluggable strategies

`TierEvaluationService` receives all `TierCriteriaStrategy` beans via constructor injection and builds an `EnumMap<CriteriaType, TierCriteriaStrategy>`. Adding a new criterion type (e.g. `LIFETIME_VALUE`) requires a new enum value and a new `@Component` — the service discovers it automatically.

---

---

*Built with Spring Boot 3.5 · Java 21 · H2 · Gradle*
