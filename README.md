Solution: AI Telehealth Mobile App
Doctari has been designed to be your personal health companion, providing AI-driven healthcare solutions at your fingertips. Also, it lets you connect with verified specialists, access expert advice, book appointments, and manage your health effectively. Our app offers a comprehensive suite of features including symptom assessment, treatment plans, fitness guidance, mental health support, and telehealth consultations. Experience the future of healthcare with Doctari.
Technologies used in the development
	Android Gradle: This suite of libraries provides architectural components, UI tools, and foundation classes to build high-quality apps efficiently.
•	Navigation: For managing app navigation between screens.
•	Lifecycle: For handling activity and fragment lifecycle states.

	Firebase: For authentication, real-time database, cloud messaging, and other backend services.
•	Firebase Authentication: For OTP-based login and user management.
•	Firebase Realtime Database: For real-time chat and data synchronization.
•	Firebase Cloud Messaging: For push notifications.

	Camera2 API: For capturing and processing images.
	Media Recorder: For recording video calls.
	WebRTC: For peer-to-peer communication (for video calls).
	AI and Machine Learning
•	AI Framework: Gemini (flash model)

Main Features:
SplashActivity:
•	Function: Introduces the app, displays the logo, and prepares the app's initial state.
LoginActivity:
o	Function: Secure user login and registration with multiple methods, including email, social media, and biometrics.
o	OTP-based authentication using Firebase Authentication for enhanced security.
o	Username setting after OTP verification.
RegistrationActivity:
•	Function: Allows users to register as either a patient or a specialist after successful authentication.
MainActivity:
o	Function: Central hub of the app displaying a personalized dashboard with key health metrics and quick access to main features.
o	Dashboard section displays subfolders.
o	Specialist tab groups specialists by specialization.
o	Bottom bar with Home, Doctari, Chats, and Support segments.
DoctariActivity:
o	Function: Offers AI-powered specialist solutions based on text and image inputs.
o	Utilizes Gemini API for advanced AI capabilities.
ChatsActivity:
o	Function: Facilitates real-time communication between specialists and patients.
o	Supports chat and video calls using Firebase Realtime Database and WebRTC.
SupportActivity:
o	Function: Handles technical issues and user reports.
o	Provides a platform for users to address app-related problems.
AppointmentActivity:
•	Function: Allows patients to book appointments with specialists for one-on-one video consultations.
SpecialistDetailsActivity:
•	Function: Displays detailed information about specialists, including verification status, for informed patient decision-making.
NotificationActivity:
•	Function: Manages and displays message notifications.

Additional Features:
PatientAssessmentActivity:
•	Function: Collects symptom input via text and images, generates a differential diagnosis list using the Gemini API, and passes data for assessment.
TreatmentPlanActivity:
•	Function: Generates personalized treatment plans based on patient assessment data and provides AI-powered suggestions using the Gemini API.
HealthRecommendationsActivity:
•	Function: Offers AI-powered health tips and recommendations tailored to user data and health goals.
FitnessPlanActivity:
•	Function: Generates and displays personalized workout plans, tracks progress, and provides real-time feedback.
MentalHealthSupportActivity:
•	Function: Provides AI-driven mental health support, including chat-based counseling, mindfulness exercises, and mood tracking.
TelehealthActivity:
•	Function: Facilitates virtual consultations with healthcare professionals, including video calls and health record management.
MedicationReminderActivity (next update)
•	Function: Manages medication schedules, sends reminders, and allows users to log intake and track adherence.

HealthMetricsActivity: (next update)
•	Function: Displays detailed health metrics and historical data, providing insights and trends.
SettingsActivity:
•	Function: Configures app preferences, notifications, privacy settings, and wearable device integrations.
ProfileActivity:
•	Function: Manages user information, health goals, preferences, profile pictures, and social media links.
AboutActivity:
•	Function: Provides information about the app, developers, terms of service, privacy policy, and support resources.
Improved Project Structure:
•	Clear separation of concerns between authentication, registration, and main app features.
•	Enhanced user experience with a streamlined onboarding process.
•	Improved security with OTP-based authentication.
•	Comprehensive feature set for both patients and specialists.
•	Effective use of AI through the Gemini API for various functionalities.
•	Robust communication and support mechanisms.
