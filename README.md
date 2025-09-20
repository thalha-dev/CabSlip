# CabSlip 🚕

A modern Android application for cab/taxi operators to generate professional digital receipts with signatures and PDF sharing capabilities.

## 📱 Overview

CabSlip is a comprehensive receipt management system designed specifically for cab and taxi operators. The app allows drivers and cab operators to create, manage, and share professional receipts with their customers instantly. Built with modern Android technologies including Jetpack Compose and Material 3 design system.

## ✨ Features

### Core Functionality
- **📄 Digital Receipt Generation** - Create professional receipts with all trip details
- **✍️ Digital Signatures** - Capture customer signatures directly on the device
- **📤 PDF Export & Sharing** - Generate and share receipts as PDF files
- **🔍 Receipt Management** - View, search, and edit existing receipts
- **🏢 Business Profile** - Configure cab company information and branding

### Receipt Details
- **Trip Information**: Boarding location, destination, start/end times
- **Fare Calculation**: Price per KM, total distance, waiting charges
- **Additional Charges**: Toll & parking fees, bata (driver allowance)
- **Driver Details**: Driver name and contact information
- **Vehicle Information**: Vehicle number and registration details
- **Automatic Calculations**: Real-time fare computation with breakdown

### User Experience
- **📱 Material 3 Design** - Modern, clean interface following Google's design guidelines
- **🌙 Adaptive Theming** - Supports system light/dark themes
- **📊 Visual Summaries** - Clear fare breakdowns and totals
- **🗓️ Date/Time Pickers** - Easy date and time selection with Material Design pickers
- **🔄 Real-time Updates** - Live calculations as you input data

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
- **PDF Generation** - Custom PDF creation for receipts
- **File I/O** - Image and signature file management
- **Share Integration** - Android system sharing for PDF files
- **Canvas Drawing** - Custom signature capture functionality

## 📋 Requirements

- **Android 10 (API level 29)** or higher
- **Storage Permission** - For saving signatures and PDFs
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
5. **Capture signature** (optional):
   - Draw customer signature on the screen
6. **Review fare summary** and save

### Managing Receipts

- **View Recent Receipts** - Home screen shows latest 6 receipts
- **Browse All Receipts** - Receipts tab lists all generated receipts
- **Search Functionality** - Find receipts by location, vehicle, or ID
- **Edit Receipts** - Modify existing receipt details
- **Share as PDF** - Generate and share professional PDF receipts

### PDF Features

Generated PDFs include:
- Company logo and branding
- Complete trip and fare details
- Digital signature (if captured)
- Professional formatting suitable for business use

## 🏗️ Project Structure

```
app/src/main/java/dev/thalha/cabslip/
├── data/
│   ├── database/         # Room database configuration
│   ├── entity/          # Data models (Receipt, CabInfo)
│   └── repository/      # Data access layer
├── ui/
│   ├── components/      # Reusable UI components
│   ├── navigation/      # Navigation setup
│   ├── screens/         # Main app screens
│   └── theme/          # Material 3 theming
└── utils/              # Utility classes (PDF, Share, etc.)
```

### Key Components

- **`Receipt.kt`** - Core data model for receipt information
- **`CabInfo.kt`** - Business information data model
- **`CabSlipRepository.kt`** - Data access and business logic
- **`SignatureCapture.kt`** - Custom signature drawing component
- **`PdfGenerator.kt`** - PDF creation and formatting
- **`CabSlipNavigation.kt`** - App navigation structure

## 🎨 Screenshots

*Coming soon - Screenshots of the app in action*

## 🛣️ Roadmap

### Version 1.0 (Current)
- ✅ Basic receipt creation and management
- ✅ Digital signature capture
- ✅ PDF generation and sharing
- ✅ Material 3 design implementation

### Future Enhancements
- 📊 **Analytics Dashboard** - Monthly/yearly earnings reports
- ☁️ **Cloud Backup** - Sync receipts across devices
- 📧 **Email Integration** - Send receipts directly via email
- 🌐 **Multi-language Support** - Localization for different regions
- 💰 **Tax Calculations** - Automatic tax computation
- 📱 **Customer App** - Companion app for customers to receive receipts

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
- **Dependency Injection** - Manual DI with factory patterns

### Database Schema
- **receipts** table - Stores all receipt information
- **cab_info** table - Single row for business configuration
- **Room ORM** - Type-safe database operations with compile-time verification

### Performance Optimizations
- **Lazy Loading** - Efficient list rendering with LazyColumn
- **State Management** - Optimized recomposition with remember/mutableStateOf
- **Background Operations** - Database operations on IO dispatcher
- **Memory Management** - Proper lifecycle-aware components

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Thalha** - [@thalha](https://github.com/thalha-dev)

## 🙏 Acknowledgments

- **Jetpack Compose Team** - For the amazing declarative UI toolkit
- **Material Design Team** - For the comprehensive design system
- **Android Developers Community** - For continuous inspiration and support

## 📞 Support

If you encounter any issues or have feature requests:

1. Check existing [Issues](https://github.com/yourusername/cabslip/issues)
2. Create a new issue with detailed description
3. Include device information and logs if reporting bugs

## 🔗 Links

- [Android Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material 3 Design](https://m3.material.io/)
- [Room Database](https://developer.android.com/training/data-storage/room)

---

**Made with ❤️ for the cab operator community**

*CabSlip - Professional receipts made simple*
