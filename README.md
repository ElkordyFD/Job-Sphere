# Job Sphere

Job Sphere is a simple Java Desktop Application for job searching and application management.
It connects Applicants with Companies in a streamlined environment.

## Features
- **User Authentication**: Register and Login as Applicant or Company.
- **Applicant Features**: Search for jobs by keyword, view details, and apply.
- **Company Features**: Post new jobs, view received applications, and manage application status.
- **Design Patterns**: Implements 6 core design patterns.

## Design Patterns Used
1. **Singleton**: `DataManager` ensures a single instance of data storage.
2. **Factory**: `UserFactory` creates `Applicant` or `Company` objects based on input.
3. **Builder**: `JobBuilder` constructs complex `Job` objects step-by-step.
4. **Strategy**: `SearchStrategy` allows switching between search algorithms (e.g., Keyword Search).
5. **State**: `ApplicationState` manages the lifecycle of a job application (Applied -> Reviewed -> Accepted/Rejected).
6. **Proxy**: `LoginProxy` adds validation, logging, and rate limiting to the login process.

## How to Run
1. Ensure you have Java installed.
2. Run the `run.bat` file.
   OR
3. Compile and run manually:
   ```bash
   javac -d bin src/com/jobsphere/core/*.java src/com/jobsphere/ui/*.java
   java -cp bin com.jobsphere.ui.MainFrame
   ```

## Usage
1. **Register**: Create a Company account (e.g., "TechCorp") and an Applicant account (e.g., "JohnDoe").
2. **Post Job**: Login as Company, go to "Post New Job".
3. **Apply**: Login as Applicant, search for the job, and click "Apply".
4. **Manage**: Login as Company, go to "Manage Applications", and move the application to the next stage.
