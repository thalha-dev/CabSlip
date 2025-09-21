# CabSlip 🚕

A modern Android application for cab/taxi operators to generate professional digital receipts with centralized signature management and data backup capabilities.

## 📱 Overview

CabSlip is a comprehensive receipt management system designed specifically for cab and taxi operators. The app allows drivers and cab operators to create, manage, and share professional receipts with their customers instantly. Built with modern Android technologies including Jetpack Compose and Material 3 design system.

## ✨ Features

### Core Functionality
- **📄 Digital Receipt Generation** - Create professional receipts with all trip details
- **✍️ Centralized Owner Signature** - Set owner signature once in business profile, automatically applied to all receipts
- **📤 PDF Export & Sharing** - Generate and share receipts as PDF files
- **🔍 Receipt Management** - View, search, and edit existing receipts
- **🏢 Business Profile** - Configure cab company information, branding, and owner signature
- **💾 Data Backup & Restore** - Export and import all receipt data for device transfers

### Receipt Details
- **Trip Information**: Boarding location, destination, start/end times
- **Fare Calculation**: Price per KM, total distance, waiting charges
- **Additional Charges**: Toll & parking fees, bata (driver allowance)
- **Driver Details**: Driver name and contact information
- **Vehicle Information**: Vehicle number and registration details
- **Automatic Calculations**: Real-time fare computation with breakdown
- **Owner Signature**: Automatically included from business profile

### Data Management
- **🔄 Backup & Restore** - Export all receipt data to JSON file for safe keeping
- **📱 Device Transfer** - Easily move all data when getting a new device
- **☁️ Settings Screen** - Centralized data import/export functionality
- **💾 No Signature Files** - Simplified backup without complex file handling

### User Experience
- **📱 Material 3 Design** - Modern, clean interface following Google's design guidelines
- **🌙 Adaptive Theming** - Supports system light/dark themes
- **📊 Visual Summaries** - Clear fare breakdowns and totals
- **🗓️ Date/Time Pickers** - Easy date and time selection with Material Design pickers
- **🔄 Real-time Updates** - Live calculations as you input data
- **⚙️ Easy Settings Access** - Settings icon on home screen for quick navigation

## 🛠️ Technology Stack

### Frontend
- **Jetpack Compose** - Modern declarative UI toolkit
- **Material 3** - Latest Material Design components
- **Navigation Compose** - Type-safe navigation between screens
- **State Management** - Reactive state handling with Compose

### Backend & Storage
- **Room Database** - Local SQLite database with type-safe queries
- **Kotlin Coroutines** - Asynchronous programming and background tasks
- **Repository Pattern** - Clean architecture with data abstraction
- **KSP (Kotlin Symbol Processing)** - Compile-time code generation

### Additional Features
- **PDF Generation** - Custom PDF creation for receipts with centralized signatures
- **File I/O** - Logo and signature file management
- **Share Integration** - Android system sharing for PDF files
- **Canvas Drawing** - Custom signature capture functionality
- **JSON Serialization** - Kotlin Serialization for backup/restore functionality

## 📋 Requirements

- **Android 10 (API level 29)** or higher
- **Storage Permission** - For saving signatures, PDFs, and backup files
- **64 MB RAM** - Minimum memory for smooth operation
- **10 MB Storage** - For app installation and data

## 🚀 Getting Started

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/cabslip.git
   cd cabslip
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build and Run**
   - Connect your Android device or start an emulator
   - Click "Run" or press `Ctrl+R`

### First Time Setup

When you first launch CabSlip, you'll be guided through a one-time setup:

1. **Company Information**
   - Cab company name
   - Business address
   - Contact details (primary & secondary)
   - Email address

2. **Logo Upload** (Optional)
   - Upload your company logo for professional receipts

3. **Owner Signature** (Required)
   - Draw your signature once - it will be automatically added to all receipts

## 📖 Usage Guide

### Creating a Receipt

1. **Tap the "Create" (+) button** in the bottom navigation
2. **Fill in trip details**:
   - Boarding location and destination
   - Trip start date/time (end time optional)
   - Vehicle number
3. **Add driver information** (optional):
   - Driver name and mobile number
4. **Enter fare details**:
   - Total kilometers traveled
   - Price per kilometer
   - Waiting hours and charges
   - Toll/parking fees and bata
5. **Review fare summary** and save
6. **No signature needed** - Your signature from business profile is automatically included

### Managing Receipts

- **View Recent Receipts** - Home screen shows latest 6 receipts
- **Browse All Receipts** - Receipts tab lists all generated receipts
- **Search Functionality** - Find receipts by location, vehicle, or ID
- **Edit Receipts** - Modify existing receipt details (no signature management needed)
- **Share as PDF** - Generate and share professional PDF receipts with owner signature

### Data Backup & Restore

1. **Access Settings** - Tap settings icon on home screen or navigate to Settings tab
2. **Export Data**:
   - Tap "Export Data" to create a backup file
   - Choose location to save the JSON backup file
   - All receipts and business info are included
3. **Import Data**:
   - Tap "Import Data" on new device
   - Select your backup JSON file
   - All data including business profile and receipts are restored
   - ⚠️ **Warning**: Import will replace ALL existing data

### Business Profile Management

- **Access via Settings** - Navigate to "Cab Info" in settings
- **Update Signature** - Modify your signature that appears on all receipts
- **Logo Management** - Change company logo
- **Contact Updates** - Update business information as needed

### PDF Features

Generated PDFs include:
- Company logo and branding
- Complete trip and fare details
- Owner signature from business profile (automatically included)
- Professional formatting suitable for business use

## 🏗️ Project Structure

```
app/src/main/java/dev/thalha/cabslip/
├── data/
│   ├── database/         # Room database configuration
│   ├── entity/          # Data models (Receipt, CabInfo)
│   ├── model/           # Backup data models
│   └── repository/      # Data access layer
├── ui/
│   ├── components/      # Reusable UI components
│   ├── navigation/      # Navigation setup
│   ├── screens/         # Main app screens
│   └── theme/          # Material 3 theming
└── utils/              # Utility classes (PDF, Share, Backup, etc.)
```

### Key Components

- **`Receipt.kt`** - Core data model for receipt information (no signature field)
- **`CabInfo.kt`** - Business information data model (includes owner signature)
- **`BackupData.kt`** - Data model for backup/restore functionality
- **`CabSlipRepository.kt`** - Data access and business logic
- **`SignatureCapture.kt`** - Custom signature drawing component
- **`PdfGenerator.kt`** - PDF creation with centralized signature handling
- **`BackupRestoreUtils.kt`** - Backup and restore functionality
- **`SettingsScreen.kt`** - Data management and settings
- **`CabSlipNavigation.kt`** - App navigation structure with settings

## 🎨 Key Changes in Latest Version

### Centralized Signature Management
- **Owner signature** moved from individual receipts to business profile
- **One-time setup** - Draw signature once, use everywhere
- **Simplified workflow** - No signature management needed when creating receipts
- **Consistent branding** - Same signature on all receipts automatically

### Enhanced Backup & Restore
- **Complete data export** - All receipts and business info in one JSON file
- **Device transfer** - Easy migration to new devices
- **No file complexity** - Simplified backup without signature file handling
- **Settings integration** - Backup/restore accessible from settings screen

### Improved Navigation
- **Settings tab** - Dedicated settings section in bottom navigation
- **Settings icon** - Quick access from home screen
- **Organized structure** - Better separation of concerns

## 🛣️ Roadmap

### Version 1.0 (Current)
- ✅ Basic receipt creation and management
- ✅ Centralized owner signature management
- ✅ PDF generation with automatic signature inclusion
- ✅ Complete backup and restore functionality
- ✅ Material 3 design implementation
- ✅ Settings screen with data management

### Future Enhancements
- 📊 **Analytics Dashboard** - Monthly/yearly earnings reports
- ☁️ **Cloud Backup** - Sync receipts across devices
- 📧 **Email Integration** - Send receipts directly via email
- 🌐 **Multi-language Support** - Localization for different regions
- 💰 **Tax Calculations** - Automatic tax computation
- 📱 **Customer App** - Companion app for customers to receive receipts
- 🔐 **Data Encryption** - Enhanced security for backup files

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit your changes** (`git commit -m 'Add amazing feature'`)
4. **Push to the branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

### Development Guidelines
- Follow Kotlin coding conventions
- Use Jetpack Compose best practices
- Write meaningful commit messages
- Test your changes thoroughly
- Update documentation as needed

## 🔧 Technical Details

### Architecture
- **MVVM Pattern** - Clean separation of concerns
- **Repository Pattern** - Centralized data access
- **Unidirectional Data Flow** - Predictable state management
- **Manual Dependency Injection** - Factory patterns for clean architecture

### Database Schema
- **receipts** table - Stores receipt information (no signature field)
- **cab_info** table - Business configuration with owner signature
- **Room ORM** - Type-safe database operations with compile-time verification
- **Database Migrations** - Handled automatically with fallback strategy

### Performance Optimizations
- **Lazy Loading** - Efficient list rendering with LazyColumn
- **State Management** - Optimized recomposition with remember/mutableStateOf
- **Background Operations** - Database operations on IO dispatcher
- **Memory Management** - Proper lifecycle-aware components
- **Simplified Backup** - No complex file handling, just JSON serialization

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Thalha** - [@thalha](https://github.com/thalha-dev)

## 🙏 Acknowledgments

- **Jetpack Compose Team** - For the amazing declarative UI toolkit
- **Material Design Team** - For the comprehensive design system
- **Android Developers Community** - For continuous inspiration and support
- **Kotlin Serialization Team** - For excellent JSON handling capabilities

## 📞 Support

If you encounter any issues or have feature requests:

1. Check existing [Issues](https://github.com/yourusername/cabslip/issues)
2. Create a new issue with detailed description
3. Include device information and logs if reporting bugs

## 🔗 Links

- [Android Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material 3 Design](https://m3.material.io/)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Kotlin Serialization](https://kotlinlang.org/docs/serialization.html)

---

**Made with ❤️ for the cab operator community**

*CabSlip - Professional receipts made simple with centralized signature management*
