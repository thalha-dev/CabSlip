# CabSlip — Cab Receipt App — Requirements

## Overview

This document describes a **generic cab/ taxi owner** mobile application that any cab owner can use
to create, store, view and share professional trip receipts. The document is written in Markdown so
an AI or developer can understand the scope, phased implementation plan, UI/UX expectations, data
model, validation rules and integration points.

> Goal: Build a compact, offline-first Android app (single-device) that stores cab information and
> trip receipts in a local Room database, produces polished PDF receipts, and lets the owner share
> them with other apps. The app uses a bottom navigation with a centered action button to create new
> receipts.
>

---

# Phases (ordered)

## Phase 1 — Minimum Viable Product (MVP)

**Purpose:** Get a working flow for first-run registration, saving cab info, creating receipts and
storing them locally.

1. First-run flow
    - Show splash/loading screen.
    - Detect first-run: if this is the first time, open **Register Cab Info** screen (required
      fields must be filled before continuing).
    - Persist cab info to local DB.
2. Basic navigation & screens
    - Bottom navigation with three tabs: **Home**, **Receipts**, **Cab Info**.
    - Center floating action (plus) button to create new receipt (navigates to Create Receipt
      screen).
3. Receipt creation
    - A Create Receipt screen with required fields (see Receipt Fields section).
    - Save receipts to Room DB.
4. Receipt list & details
    - Home shows a few recent receipts.
    - Receipts page lists all receipts (search and basic filter by date range).
    - Tapping a receipt opens its detail page.
5. Offline persistence
    - Use Room DB for all cab info and receipts.

Deliverable of Phase 1: A stable app that registers a cab, creates receipts, lists receipts and
saves everything locally.

---

## Phase 2 — Edit, Update & PDF Export

**Purpose:** Improve user workflows, add editing, PDF generation and share features.

1. Edit Flow
    - Allow editing of saved receipts (open the receipt and provide Edit mode).
    - Editing an existing receipt updates the DB record (maintain the original receipt number).
      Provide a clear `Save` vs `Cancel` UX.
2. Signature capture
    - Implement a drawable signature input on Create/Edit Receipt screen for the cab owner
      signature.
    - Save signature as an image (PNG) in app local storage or as blob within Room (recommend
      storing as a file and path reference in DB).
3. PDF generation
    - Generate a professional-looking PDF receipt from a receipt record + cab info + signature.
    - Provide a `Share` button that exports the PDF and launches Android share sheet (share to
      email, WhatsApp, drive, etc.).
4. Improvements to UI
    - Polish layout and typography for the generated PDF and in-app receipt preview.

Deliverable of Phase 2: Users can edit receipts, capture signature, export a PDF and share it.

---

## Phase 3 — Search, Filters, Validation, UX polish

**Purpose:** Make it easy to operate the app daily and prevent data inconsistencies.

1. Search & filtering
    - Search receipts by driver name, receipt id, destination.
    - Filter receipts by date range (from — to).
2. Validation
    - Strong input validation (required fields, numeric ranges, phone number formats).
    - Prevent negative numbers, ensure date logic (start date ≤ end date), and ensure total km and
      prices are non-negative.
3. UX polish
    - Show helpful empty states, loading states, confirmations (Saved, Updated, Deleted) and undo
      where possible.
    - Provide friendly error messages.
4. Export options & preview
    - Provide PDF preview before sharing.

Deliverable of Phase 3: Highly usable searching, reliable validation and polished usability.

---

## Phase 4 — Extras & Optional Enhancements

**Purpose:** Add quality-of-life features and export/import.

1. Backup & restore
    - Implement optional local backup (export DB file) and restore.
2. Export CSV of receipts
    - Allow export of receipts as CSV for bookkeeping.
3. Multi-language support / Localization
    - Add support for multiple locales (e.g., English + user-preferred language).
4. Small account settings
    - App theme (light/dark), currency selection, number formatting.
5. Analytics (local)
    - Simple stats on the Home screen: total trips, income for period, recent drivers.

Deliverable of Phase 4: Backup, export and small but valuable convenience features.

---

# Screens & Navigation

- **Navigation style:** Bottom Navigation with three tabs and a centered floating action button (
  FAB).
    - Left tab: **Home** (recent receipts)
    - Center: **Create New Receipt** (FAB — plus icon)
    - Right tab: **Receipts / List** (full list, search & filters)
    - Secondary: **Cab Info** (either as the third tab or accessible via the right-most tab or
      overflow; the user requested three pages — Home, Receipts and Cab Info — so place Cab Info as
      rightmost tab and use the FAB as center)

### Home screen

- Title: `Home` or `Recent Receipts`
- Displays the most recent 4–6 receipts in a card list with: receipt id, destination, date, total
  fee and small icon for signature present/missing.
- Clicking a card opens the Receipt Detail/Edit page.

### Receipts (Listing) screen

- List of all receipts (in descending chronological order by trip start date or creation time).
- Search bar (search across driver name, receipt id, destination).
- Date range filter in the top bar or via a filter icon (open a compact modal).
- Each row shows: receipt id, date, driver name, destination, total fee.
- Tapping a row opens the Receipt Detail/Edit screen.

### Create/Edit Receipt screen

- Fields (see Receipt Fields section below).
- Signature drawable control.
- `Save` button (create new or update existing — behavior depends on navigation source).
- `Share` button (after save) to generate a PDF and open share sheet.
- If opened to Edit, populate fields and allow `Update`.

### Cab Info screen

- Fields to enter the cab owner’s details recorded at first-run and editable here.
- Logo upload control (png/jpeg), show preview and remove/replace option.
- Save button to persist.
- Display example formatted contact block (for PDF/header).

---

# Receipt Fields (data model)

All fields must be stored. Required fields marked with `*`.

- **receiptId** (string) — system generated, unique. Example format: `1750680320562-AW0D4V`.
    - Generation rule: `{timestamp_ms}-{6 char uppercase alphanumeric}`
    - Guarantee uniqueness by combination of time in millis + random component. Example algorithm
      presented below.
- **boardingLocation** (string)*
- **destination** (string)*
- **tripStartDate** (ISO datetime / date)*
- **tripEndDate** (ISO datetime / date)
- **pricePerKm** (decimal / double)*
- **waitingChargePerHr** (decimal / double)
- **waitingHrs** (decimal) — allow fractional hours (e.g. 0.5)
- **totalKm** (decimal)*
- **tollParking** (decimal)
- **bata** (decimal)
- **driverName** (string)
- **driverMobile** (string) — validate phone format
- **vehicleNumber** (string)*
- **ownerSignaturePath** (string) — path to saved signature image
- **createdAt** (timestamp)
- **updatedAt** (timestamp)

### Calculated fields (do not require user input)

- **baseFare** = `pricePerKm * totalKm`
- **waitingFee** = `waitingChargePerHr * waitingHrs`
- **totalFee** = `baseFare + tollParking + bata + waitingFee`

**Notes:** Display all currency values consistently. Provide rounding rules (e.g., round to 2
decimal places). Allow the user to set currency symbol in settings (Phase 4).

---

# Receipt ID generation example (pseudocode)

```
// Pseudocode
long timestamp = System.currentTimeMillis(); // e.g. 1750680320562
String random6 = generateRandomUpperAlphaNum(6); // e.g. AW0D4V
String receiptId = timestamp + "-" + random6;

```

`generateRandomUpperAlphaNum` picks from `A-Z0-9` and returns exactly 6 characters. This is
sufficient for single-device uniqueness combined with timestamp.

---

# Data Persistence (Room DB)

**Entities:**

- `CabInfo` — single row storing the cab details
    - id (PK int, constant 1)
    - cabName
    - cabAddress
    - primaryContact
    - secondaryContact (nullable)
    - email
    - logoPath (nullable)
    - createdAt
    - updatedAt
- `Receipt` — receipts table
    - receiptId (PK string)
    - boardingLocation
    - destination
    - tripStartDate
    - tripEndDate
    - pricePerKm
    - waitingChargePerHr
    - waitingHrs
    - totalKm
    - tollParking
    - bata
    - driverName
    - driverMobile
    - vehicleNumber
    - ownerSignaturePath
    - baseFare
    - waitingFee
    - totalFee
    - createdAt
    - updatedAt

**DAOs & Methods (examples)**

- `CabInfoDao` — `insertOrUpdateCabInfo(cabInfo)`, `getCabInfo()`
- `ReceiptDao` — `insertReceipt(receipt)`, `updateReceipt(receipt)`, `getReceiptById(id)`,
  `searchReceipts(query)`, `filterByDateRange(from, to)`, `getRecentReceipts(limit)`

**Storage of images**

- Recommended: save images (logo, signature) to app's internal files directory and store the file
  path in the DB. This avoids large blob storage inside the Room DB and simplifies file sharing for
  the PDF.

---

# UX & UI Guidelines

- **Look & Feel:** Very professional — clean typography, enough whitespace, subtle shadows,
  consistent card layout.
- **Receipt visual style:** Use a neat header with cab logo (left), cab name and contact block (
  right), a bold receipt id and date, a table/list for trip details, calculation breakdown and a
  footer with signature and "Powered by" small text.
- **Logo recommendations:** Accept PNG/JPEG. Optimal size for logo upload: **512×512 px (square)**,
  keep file size under **200 KB**. If larger, show recommended crop/resize UI (scale down to 512×512
  on upload).
- **Signature:** Smooth drawable area with an eraser and Clear button. Require a minimum stroke
  length to ensure an actual signature was drawn.
- **Typography & Colors:** Use system fonts and a neutral palette; keep strong contrast for
  readability.
- **Accessibility:** Ensure text sizes are adjustable and controls large enough for fingers. Provide
  content descriptions for important images.

---

# PDF Format & Export

- **Page size:** Use an A4 or receipt-sized PDF depending on user preference. Default: A4 portrait
  with margins.
- **Elements on PDF:** Cab header (logo, name, address, contacts), receipt id & date, trip detail
  table, fare breakdown (with formulas shown), signature image, notes/tax/terms section.
- **File naming:** `${receiptId}_${tripStartDate}.pdf` (ISO date format in filename).
- **Share action:** After generating PDF, open system share sheet.
- **PDF generation library:** On Android, consider `PdfDocument` (native) or a third-party library
  like iText / PdfBox / AndroidPdfWriter. Keep license considerations in mind (iText has AGPL vs
  paid options).

---

# Permissions & Platform Concerns (Android-focused)

- **Storage & Files:** On modern Android versions (Android 11+), use scoped storage APIs. For saving
  to internal app storage, no runtime permission is needed. If allowing the user to save to external
  storage or pick images from gallery, request the appropriate `READ_MEDIA_IMAGES` /
  `READ_EXTERNAL_STORAGE` permissions (platform dependent).
- **Camera:** Optional — if you allow taking a photo for logo, request camera permission.
- **Export & Sharing:** Use `FileProvider` for secure sharing of internally created PDFs.

---

# Validation Rules

- Required fields must be non-empty: cab name, address, primary contact, email, vehicle number,
  boarding & destination, price per km, total km.
- Numeric fields must be positive or zero as appropriate.
- Phone number: validate length and digits (optionally allow +country prefix).
- Dates: tripEndDate must not be before tripStartDate.
- Signature: ensure user has drawn at least one stroke before allowing save.

---

# Error / Edge Cases

- Duplicate receiptId collision (extremely rare): if generated receiptId already exists, generate
  again with new random part.
- Corrupt / missing image file: show placeholder and allow re-attach.
- Data migration for future versions: include Room migration plan if schema changes.

---

# Testing Checklist

- First-run registration: mandatory fields, saving, and seeing Home screen.
- Create receipt: all required fields and formula calculation.
- Edit receipt: update fields and confirm values and PDF reflect changes.
- Signature capture: draw, clear, re-sign.
- PDF generation & share: file correctness and image rendering.
- Search & filter: accuracy and performance with many receipts.
- Backup/restore: verify restore rehydrates receipts and images.

---

# Sample Receipt (textual example)

**Cab Name:** AT CABS at top center
**Address:** 123 Main St, City, Country (center, under cab name)
**Contact:** +91-9876543210 | ... 
**Email:**

**Receipt ID:** 1750680320562-AW0D4V

**Boarding:** Airport Terminal A

**Destination:** Downtown Hotel

**Driver:** Rahim (9000000000)

**Trip:** 2025-09-20 09:00 — 2025-09-20 09:50

**Total KMs:** 15.5

**Price/km:** 12.00

**Base fare:** 186.00

**Waiting:** 1.0 hr × 30.00/hr = 30.00

**Toll & Parking:** 20.00

**Bata:** 50.00

**Total Fee:** ₹286.00

(Signature image here)

## Tech Notes
- AndroidX Room for local DB.
- iText 7.2.6 Android core library for PDF generation.
- Kotlin coroutines for async DB operations.
- Jetpack components for UI and lifecycle management.