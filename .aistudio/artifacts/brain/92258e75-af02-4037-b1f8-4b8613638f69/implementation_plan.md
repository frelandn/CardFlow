# CardFlow - Credit Card Expense & Statement Tracker

CardFlow is an offline-first Android expense tracker built specifically for credit cards, helping users monitor multiple credit cards, track statement billing cycles and payment due dates, analyze category spending, and optimize credit utilization in a private, sleek fintech interface.

> [!IMPORTANT]
> **User Review & Confirmed Decisions**
> - **Core Feature Focus**: Multi-card tracking, statement billing cycles, and spending analytics.
> - **Data Persistence**: 100% offline-first local database using **Room Database** with Kotlin Coroutines Flow for maximum privacy and zero cloud dependence.
> - **Visual Direction**: Sleek dark luxury fintech aesthetic with glowing card gradients, neon mint & amber accents, and crisp Material Design 3 cards matching the preview mockup.
> - **Preloaded Starter Data**: The database will seed 2 realistic sample cards (e.g., "Sapphire Reserve", "Gold Rewards") and typical transactions so the app is immediately visual, informative, and interactive on initial launch.

---

## 1. Overview & Core Concept

- **What It Does**: CardFlow allows users to manage multiple credit cards in one place, log purchases with merchant/category/card tags, track credit limits and utilization percentages, monitor statement closing and payment due countdowns, and gain insights with monthly expense breakdowns.
- **Target Audience**: Anyone managing one or more credit cards who wants to avoid surprise bills, never miss a payment due date, keep their credit score healthy by staying under 30% credit utilization, and understand their spending habits without linking sensitive bank credentials.
- **Key Value**: Specialized for credit cards (unlike generic expense apps)—emphasizing statement closing dates, interest-free grace periods, credit utilization warnings, and per-card balances.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Dashboard (Home)**:
   - **Interactive Card Carousel**: Horizontal pager displaying styled virtual credit cards with card brand chips (Visa, Mastercard, Amex), custom gradients, masked numbers (`•••• 6789`), available credit, total limit, and statement closing & payment due indicators.
   - **Utilization Gauge**: Dynamic radial/arc meter showing overall credit utilization (e.g. 21% Healthy < 30%, Warning 30-50%, Critical > 50%).
   - **Quick Stat Summary**: Monthly spend total, statement balance, and next upcoming payment date countdown.
   - **Category Quick Filters & Recent Feed**: Scrollable pill chips and recent transactions with merchant logos, date, category tag, card badge, and transaction amount.

2. **Add / Edit Transaction Sheet**:
   - Modal bottom sheet with quick keypad or amount input, merchant name, category selection (Dining, Groceries, Shopping, Travel, Utilities, Entertainment, Health, Other), card picker, date picker, and optional note.
   - Immediate reactive update to the card balance, available credit, and category totals.

3. **Analytics & Statement Insights**:
   - Monthly and billing-cycle spending breakdown with interactive category bars/donuts.
   - Top spending merchants and comparison against previous statement cycle.
   - Credit utilization breakdown per individual card.

4. **Card Management (Add / Edit / Payment)**:
   - Add new cards with custom name, card network, last 4 digits, credit limit, statement closing day (e.g. 15th), and payment due day (e.g. 8th).
   - "Record Payment / Bill Pay" action to reset statement balance and free up credit line.

### Visual Identity & Theme
- **Theme Posture**: Dark fintech theme (slate `#0B0F19`, deep card surface `#161F30`, elevated surface `#1E293B`).
- **Accent Accents**: Mint Emerald (`#10B981`) for available credit and positive metrics; Vibrant Amber (`#F59E0B`) for due dates and warnings; Electric Cyan (`#06B6D4`) and Royal Violet (`#8B5CF6`) for card gradients.
- **Typography**: Clean, tabular typography for monetary amounts and percentages, bold headline weights, and legible uppercase micro-labels.
- **Feedback & Motion**: Spring-animated card swiping, animated utilization percentage bars, ripple effects on transaction rows, and subtle toast confirmations.

---

## 3. Key Product Decisions & Trade-Offs

- **Room Local Database vs. Cloud / Bank Plaid Sync**:
  - *Chosen Approach*: Room Database on SQLite with Kotlin Coroutines & Flow.
  - *Why*: User explicitly requested offline-first storage for privacy. Users never have to share banking credentials, API keys, or deal with network sync failures.
  - *Trade-off*: Requires manual entry or quick logging rather than automatic bank scraping. We mitigate this with rapid 3-tap transaction entry and preloaded demo data.
- **Single vs. Multi-Card Aggregate View**:
  - *Chosen Approach*: Unified dashboard showing aggregate metrics at top, while the card carousel allows filtering the entire transaction feed by selecting a specific card.
  - *Why*: Allows checking overall credit health in one glance while retaining granular card-specific statement tracking.

---

## 4. Technical Architecture & Data Strategy

### System Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        CardFlow UI Layer                               │
│  ┌───────────────────────┐  ┌──────────────────────┐  ┌─────────────┐  │
│  │   DashboardScreen     │  │   AnalyticsScreen    │  │ CardsScreen │  │
│  │ (Carousel, Stats, Trx)│  │ (Category, Bar Chart)│  │ (Add / Pay) │  │
│  └───────────┬───────────┘  └──────────┬───────────┘  └──────┬──────┘  │
│              └──────────────────┐      │      ┌──────────────┘         │
│                                 ▼      ▼      ▼                        │
│                           CardFlowViewModel                            │
│                 (Exposes StateFlow<CardFlowUiState>)                   │
└────────────────────────────────────────┬───────────────────────────────┘
                                         │
┌────────────────────────────────────────▼───────────────────────────────┐
│                          Repository Layer                              │
│                  CreditCardRepository & Data Mapper                    │
└────────────────────────────────────────┬───────────────────────────────┘
                                         │
┌────────────────────────────────────────▼───────────────────────────────┐
│                      Local Persistence (Room)                          │
│  ┌───────────────────────────────────┐  ┌───────────────────────────┐  │
│  │           CardDao                 │  │       TransactionDao      │  │
│  │  CreditCardEntity (Flow<List>)    │  │  TransactionEntity (Flow) │  │
│  └───────────────────────────────────┘  └───────────────────────────┘  │
│                      AppDatabase (SQLite on-device)                    │
└────────────────────────────────────────────────────────────────────────┘
```

### Data Models (Room Entities)

1. **`CreditCardEntity`**:
   - `id: Long` (Primary Key, autogenerate)
   - `cardName: String` (e.g. "Sapphire Reserve", "Gold Rewards")
   - `bankOrNetwork: String` (Visa, Mastercard, Amex, Discover)
   - `lastFourDigits: String` (e.g. "6789")
   - `creditLimit: Double` (e.g. 10000.0)
   - `statementClosingDay: Int` (Day of month, e.g. 15)
   - `paymentDueDay: Int` (Day of month, e.g. 8)
   - `gradientStartColor: Long` & `gradientEndColor: Long` (Color customization)
   - `colorPresetIndex: Int`

2. **`TransactionEntity`**:
   - `id: Long` (Primary Key, autogenerate)
   - `cardId: Long` (Foreign Key -> CreditCardEntity.id)
   - `merchant: String` (e.g. "Target", "Uber", "Amazon")
   - `amount: Double` (e.g. 89.10)
   - `category: String` (Dining, Groceries, Shopping, Travel, Utilities, Entertainment, Health, Other)
   - `dateMillis: Long` (Timestamp)
   - `notes: String`
   - `isPayment: Boolean` (False for purchase, True for credit card bill payment)

### UI State Mapping & Reactive Updates
- The `CardFlowViewModel` combines `cardDao.getAllCards()` and `transactionDao.getAllTransactions()` using Kotlin `combine`.
- Automatically computes:
  - Current statement balance per card & overall balance.
  - Available credit (`limit - currentSpend`).
  - Overall and per-card credit utilization ratio (`(totalSpent / totalLimit) * 100`).
  - Next due date countdown (days remaining until payment due).
  - Monthly spending by category for visual analytics.
