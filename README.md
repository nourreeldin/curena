# Curena Medication Management System

A comprehensive mobile health application designed to improve medication adherence and simplify healthcare management for patients and caregivers. The system provides medication tracking, real-time reminders, health monitoring, and seamless data synchronization across devices.

---

## Table of Contents

- [System Overview](#system-overview)
- [Features](#features)
  - [For Patients](#for-patients)
  - [For Caregivers](#for-caregivers)
  - [Healthcare Provider Integration](#healthcare-provider-integration)
- [License](#license)
- [Acknowledgments](#acknowledgments)

---

## System Overview

The Medication Management System is built using the **MVP (Model-View-Presenter) + Repository** architecture pattern, ensuring a clean separation of concerns and maintainable codebase. This architectural approach provides:

- **View Layer**: User interface components built with Jetpack Compose for a modern, declarative UI experience
- **Presenter Layer**: Business logic and coordination between the UI and data layers
- **Model Layer**: Domain entities representing core business objects (Medication, DoseLog, User, etc.)
- **Repository Layer**: Data abstraction that manages multiple data sources seamlessly

The Repository layer intelligently routes data operations across three sources:
- **Supabase (Cloud)**: Backend-as-a-Service for online data storage and multi-device synchronization
- **SQLite (Local)**: Offline-first local database ensuring full app functionality without internet
- **Cache Layer**: Fast-access memory cache for optimized performance

This architecture enables **offline-first functionality**, automatic data synchronization, and a robust, scalable foundation for healthcare data management.

---

## Features

### For Patients

#### 💊 Medication Management
- Add, edit, and delete medications with detailed information
- Set flexible dosage schedules (daily, weekly, specific days, intervals)
- Track medication inventory and receive refill reminders
- View complete medication history

#### 🔔 Smart Reminders
- Customizable medication reminder notifications
- Multiple notification types (push, sound, vibration)
- Snooze functionality with configurable durations
- Critical medication priority alerts

#### 📊 Adherence Tracking
- One-tap dose logging (taken, missed, skipped)
- Visual adherence calendar and statistics
- Weekly and monthly adherence reports
- Trend analysis and insights
- Exportable reports in PDF format

#### 💉 Drug Safety
- Comprehensive drug information database
- Automatic drug interaction checking
- Severity level indicators (minor, moderate, severe)
- Side effects and contraindications warnings

#### ❤️ Health Monitoring
- Track disease-specific health metrics (blood pressure, glucose, weight)
- Visual charts and graphs showing health trends
- Correlation analysis between adherence and health outcomes
- Export health data with adherence reports

#### 📅 Appointment Management
- Schedule and manage medical appointments
- Receive appointment reminders
- Link appointments to specific medications or conditions
- Store appointment history

#### 🔄 Multi-Device Sync
- Automatic data synchronization across devices
- Seamless offline-to-online transitions
- Access your data from any device

### For Caregivers

#### 👥 Patient Management
- Manage medications for multiple patients
- View all patient medication lists and schedules
- Add, edit, and update patient medications
- Monitor patient adherence remotely

#### 🚨 Real-Time Alerts
- Receive immediate notifications for missed doses
- Escalating alerts for critical medications
- Customizable alert preferences and quiet hours
- Alert history tracking

#### 📈 Adherence Monitoring
- Access patient adherence dashboards
- View detailed adherence statistics per patient
- Generate and share adherence reports
- Identify non-adherence patterns early

#### 🔐 Permission Management
- Role-based access to patient data
- Manage caregiver permissions for multiple patients
- Patient consent and authorization system

### Healthcare Provider Integration

- **Report Sharing**: Patients and caregivers can email adherence reports directly to healthcare providers
- **Data Export**: Export comprehensive medication and health data in PDF format
- **Secure Sharing**: HIPAA-compliant data sharing mechanisms
- **Read-Only Access**: Providers receive reports via email without direct system access

---

## License

This project is licensed under the **MIT License**.

```
MIT License

Copyright (c) 2025 Medication Management System Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

**Made with ❤️ for better healthcare outcomes**