# Job Sphere - Team Discussion Guide

## Team Distribution: 6 Members × 6 Design Patterns

Each member is responsible for explaining **one design pattern**, including:
- What it is and why we use it
- Core classes involved
- How it connects to the UI
- Demo steps

---

## 👤 Member 1: Singleton Pattern

### Pattern: Singleton (`DataManager`)
**Purpose:** Ensures only ONE instance of the data manager exists throughout the application.

### Core Classes:
- `DataManager.java` - The singleton class

### Key Code:
```java
public class DataManager {
    private static DataManager instance;  // Single instance
    
    private DataManager() { }  // Private constructor
    
    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
}
```

### UI Relation:
- **Used In:** `LoginPanel.java`, `ApplicantPanel.java`, `CompanyPanel.java`
- **How:** All panels call `DataManager.getInstance()` to access the same data
- **Why:** If each panel created its own DataManager, registered users wouldn't be visible across panels!

### Demo Script:
1. Open the app
2. Register a user in LoginPanel
3. Show that the same user can login - proves data is shared via singleton

---

## 👤 Member 2: Factory Pattern

### Pattern: Factory (`UserFactory`)
**Purpose:** Creates objects without exposing creation logic. The UI doesn't need to know HOW to create users.

### Core Classes:
- `UserFactory.java` - Creates Applicant or Company objects
- `User.java`, `Applicant.java`, `Company.java` - Products

### Key Code:
```java
public class UserFactory {
    public static User createUser(String type, String username, String password, String email) {
        switch (type.toUpperCase()) {
            case "APPLICANT":
                return new Applicant(username, password, email);
            case "COMPANY":
                return new Company(username, password, email);
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }
}
```

### UI Relation:
- **Used In:** `LoginPanel.java` → `handleRegister()` method
- **How:** When user selects "APPLICANT" or "COMPANY" from dropdown and clicks Register
- **Code:** `User newUser = UserFactory.createUser(role, user, pass, email);`

### Demo Script:
1. Go to Login screen
2. Select "APPLICANT" from Role dropdown
3. Fill username/password and click Register
4. Show that an Applicant object was created (login and see Applicant Dashboard)
5. Repeat with "COMPANY" to show Company Dashboard

---

## 👤 Member 3: Builder Pattern

### Pattern: Builder (`JobBuilder`)
**Purpose:** Constructs complex objects step-by-step. Jobs have many fields - Builder makes creation readable.

### Core Classes:
- `JobBuilder.java` - The builder
- `Job.java` - The product

### Key Code:
```java
public class JobBuilder {
    private String title;
    private String description;
    private String requirements;
    private String companyUsername;
    
    public JobBuilder setTitle(String title) {
        this.title = title;
        return this;  // Return self for chaining
    }
    
    public Job build() {
        return new Job(UUID.randomUUID().toString(), title, description, 
                       companyUsername, requirements);
    }
}
```

### UI Relation:
- **Used In:** `CompanyPanel.java` → "Post New Job" tab
- **How:** When company fills the form and clicks "Post Job"
- **Code:**
```java
Job job = new JobBuilder()
    .setTitle(titleField.getText())
    .setDescription(descArea.getText())
    .setRequirements(reqArea.getText())
    .setCompanyUsername(user.getUsername())
    .build();
```

### Demo Script:
1. Login as Company
2. Go to "Post New Job" tab
3. Fill Title, Description, Requirements
4. Click "Post Job"
5. Go to "Manage Jobs" tab - show the job was created

---

## 👤 Member 4: Strategy Pattern

### Pattern: Strategy (`SearchStrategy`)
**Purpose:** Allows switching between different algorithms at runtime. Today: keyword search. Tomorrow: location search!

### Core Classes:
- `SearchStrategy.java` - Interface
- `KeywordSearchStrategy.java` - Concrete implementation
- (Can add `LocationSearchStrategy`, `SalarySearchStrategy` without changing UI!)

### Key Code:
```java
// Interface
public interface SearchStrategy {
    List<Job> search(List<Job> allJobs, String query);
}

// Implementation
public class KeywordSearchStrategy implements SearchStrategy {
    @Override
    public List<Job> search(List<Job> allJobs, String query) {
        return allJobs.stream()
            .filter(job -> job.getTitle().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
    }
}
```

### UI Relation:
- **Used In:** `ApplicantPanel.java` → Search bar
- **How:** Applicant types keywords, strategy filters jobs
- **Code:**
```java
private SearchStrategy searchStrategy = new KeywordSearchStrategy();
// Later:
List<Job> filtered = searchStrategy.search(allJobs, searchField.getText());
```

### Demo Script:
1. Login as Company, post jobs: "Java Developer", "Python Developer", "Designer"
2. Logout, login as Applicant
3. Type "Java" in search box
4. Click Search - only "Java Developer" shows
5. Clear search - all jobs return

---

## 👤 Member 5: State Pattern

### Pattern: State (`ApplicationState`)
**Purpose:** Object behavior changes based on internal state. Application goes: Applied → Reviewed → Accepted/Rejected

### Core Classes:
- `ApplicationState.java` - Interface
- `AppliedState.java` - Initial state
- `ReviewedState.java` - After review
- `AcceptedState.java` - Final (accepted)
- `RejectedState.java` - Final (rejected)
- `JobApplication.java` - Context that holds state

### Key Code:
```java
// In AppliedState.java
public void next(JobApplication application) {
    application.setState(new ReviewedState());  // Transition to next state
}

// In JobApplication.java
public void next() {
    state.next(this);  // Delegate to current state
}
```

### UI Relation:
- **Used In:** `CompanyPanel.java` → "Manage Applications" tab
- **How:** Company clicks "Move to Next Stage" button
- **Code:** `app.next();` - State pattern handles the transition!

### Demo Script:
1. Login as Applicant, apply to a job
2. Logout, login as Company
3. Go to "Manage Applications"
4. See status: "Applied"
5. Click "Move to Next Stage" → Status changes to "Reviewed"
6. Click again → Status changes to "Accepted"

---

## 👤 Member 6: Proxy Pattern

### Pattern: Proxy (`LoginProxy`)
**Purpose:** Controls access to the real login service, adding validation, logging, and security features without modifying the original service.

### Core Classes:
- `LoginService.java` - Interface defining login contract
- `RealLoginService.java` - The actual login implementation
- `LoginProxy.java` - Proxy that adds extra features

### Key Code:
```java
// Interface
public interface LoginService {
    User login(String username, String password);
}

// Proxy adds validation, logging, rate limiting
public class LoginProxy implements LoginService {
    private final LoginService realLoginService;
    private int failedAttempts = 0;
    
    @Override
    public User login(String username, String password) {
        // Check for lockout
        if (isLockedOut()) {
            return null;
        }
        
        // Validate input format
        if (!validateInput(username, password)) {
            return null;
        }
        
        // Log the attempt
        System.out.println("[LoginProxy] Login attempt for: " + username);
        
        // Delegate to real service
        User user = realLoginService.login(username, password);
        
        if (user == null) {
            failedAttempts++;
            if (failedAttempts >= 5) {
                // Lock for 30 seconds
            }
        }
        return user;
    }
}
```

### UI Relation:
- **Used In:** `LoginPanel.java` → `handleLogin()` method
- **How:** When user clicks "Login", the proxy validates input before real authentication
- **What Proxy Adds:**
  1. Input validation (non-empty username/password)
  2. Username format validation (alphanumeric only)
  3. Logging of login attempts
  4. Rate limiting (locks after 5 failed attempts)

### Demo Script:
1. Try logging in with empty username - blocked by proxy
2. Try logging in with special characters in username - blocked by proxy
3. Check console for "[LoginProxy]" log messages
4. Show that valid credentials work normally
5. Try 5+ wrong passwords - account gets temporarily locked

---

## 📋 Summary Table

| Member | Pattern | Core Class | UI Component | Action |
|--------|---------|------------|--------------|--------|
| 1 | Singleton | `DataManager` | All Panels | Data sharing |
| 2 | Factory | `UserFactory` | LoginPanel | Register button |
| 3 | Builder | `JobBuilder` | CompanyPanel | Post Job form |
| 4 | Strategy | `SearchStrategy` | ApplicantPanel | Search bar |
| 5 | State | `ApplicationState` | CompanyPanel | Move to Next Stage |
| 6 | Proxy | `LoginProxy` | LoginPanel | Login validation |

---

## 🎯 Presentation Flow

**Suggested Order:**
1. **Member 1 (Singleton)** - Start with foundation
2. **Member 2 (Factory)** - Show user creation
3. **Member 6 (Proxy)** - Show login security (naturally follows registration)
4. **Member 3 (Builder)** - Post a job
5. **Member 4 (Strategy)** - Search for job
6. **Member 5 (State)** - Apply and track

Good luck with your discussion! 🚀

---

# 📂 Detailed UI Classes & Methods Reference

## 👤 Member 1: Singleton (`DataManager`)

### UI Classes & Methods Used:

| UI Class | Method | Code |
|----------|--------|------|
| `LoginPanel.java` | `handleLogin()` | `DataManager.getInstance().login(user, pass)` |
| `LoginPanel.java` | `handleRegister()` | `DataManager.getInstance().registerUser(newUser)` |
| `ApplicantPanel.java` | Constructor | `DataManager.getInstance().getNotificationService().addListener(...)` |
| `ApplicantPanel.java` | `refreshJobList()` | `DataManager.getInstance().getJobs()` |
| `ApplicantPanel.java` | Logout button | `DataManager.getInstance().logout()` |
| `CompanyPanel.java` | `refreshMyJobs()` | `DataManager.getInstance().getJobsByCompany(...)` |
| `CompanyPanel.java` | `refreshApps()` | `DataManager.getInstance().getApplicationsForJob(...)` |

**Key Point:** Every panel uses `DataManager.getInstance()` - proving singleton ensures ONE shared data source.

---

## 👤 Member 2: Factory (`UserFactory`)

### UI Classes & Methods Used:

| UI Class | Method |
|----------|--------|
| `LoginPanel.java` | `handleRegister()` |

### Code to Show:
```java
// LoginPanel.java - handleRegister() method
private void handleRegister() {
    String user = userField.getText();
    String pass = new String(passField.getPassword());
    String role = (String) roleCombo.getSelectedItem();  // "APPLICANT" or "COMPANY"

    // FACTORY PATTERN HERE!
    User newUser = UserFactory.createUser(role, user, pass, user + "@example.com");
    
    DataManager.getInstance().registerUser(newUser);
    JOptionPane.showMessageDialog(this, "Registration Successful!");
}
```

**Key Point:** The `roleCombo` dropdown provides the type string, Factory handles object creation.

---

## 👤 Member 3: Builder (`JobBuilder`)

### UI Classes & Methods Used:

| UI Class | Method |
|----------|--------|
| `CompanyPanel.java` | `createPostJobPanel()` → Post button listener |

### Code to Show:
```java
// CompanyPanel.java - inside createPostJobPanel() method
postBtn.addActionListener(e -> {
    User user = DataManager.getInstance().getCurrentUser();
    
    // BUILDER PATTERN HERE!
    Job job = new JobBuilder()
        .setTitle(titleField.getText())        // From JTextField
        .setDescription(descArea.getText())    // From JTextArea
        .setRequirements(reqArea.getText())    // From JTextArea
        .setCompanyUsername(user.getUsername())
        .build();
    
    DataManager.getInstance().addJob(job);
    JOptionPane.showMessageDialog(this, "Job Posted!");
});
```

**Key Point:** Builder chains `.setTitle()`, `.setDescription()`, etc. → cleaner than `new Job(a,b,c,d,e,f)`

---

## 👤 Member 4: Strategy (`SearchStrategy`)

### UI Classes & Methods Used:

| UI Class | Field/Method |
|----------|--------------|
| `ApplicantPanel.java` | Field: `private SearchStrategy searchStrategy` |
| `ApplicantPanel.java` | Method: `refreshJobList()` |

### Code to Show:
```java
// ApplicantPanel.java - field declaration
private SearchStrategy searchStrategy = new KeywordSearchStrategy();

// ApplicantPanel.java - refreshJobList() method
private void refreshJobList() {
    tableModel.setRowCount(0);
    List<Job> allJobs = DataManager.getInstance().getJobs();
    
    // STRATEGY PATTERN HERE!
    List<Job> filtered = searchStrategy.search(allJobs, searchField.getText());
    
    for (Job j : filtered) {
        if (j.isActive()) {
            tableModel.addRow(new Object[] { 
                j.getId(), j.getTitle(), j.getCompanyUsername(), 
                j.getDescription(), j.getRequirements() 
            });
        }
    }
}
```

**Key Point:** To add new search type, just implement `SearchStrategy` interface - no UI changes needed!

---

## 👤 Member 5: State (`ApplicationState`)

### UI Classes & Methods Used:

| UI Class | Method |
|----------|--------|
| `CompanyPanel.java` | `moveState()` |
| `CompanyPanel.java` | `refreshApps()` - displays `app.getStatus()` |

### Code to Show:
```java
// CompanyPanel.java - moveState() method
private void moveState() {
    int row = appsTable.getSelectedRow();
    if (row == -1) return;

    String jobTitle = (String) appsModel.getValueAt(row, 0);
    String applicant = (String) appsModel.getValueAt(row, 1);

    List<JobApplication> allApps = DataManager.getInstance().getApplicationsByUser(applicant);
    for (JobApplication app : allApps) {
        if (app.getJob().getTitle().equals(jobTitle)) {
            app.next();  // STATE PATTERN - delegates to current state!
            break;
        }
    }
    refreshApps();  // Table now shows new status
}
```

### State Transitions:
| Current State | After `next()` |
|---------------|----------------|
| Applied | → Reviewed |
| Reviewed | → Accepted |
| Accepted | (final - no change) |
| Rejected | (final - no change) |

---

## 👤 Member 6: Proxy (`LoginProxy`)

### UI Classes & Methods Used:

| UI Class | Method | Role |
|----------|--------|------|
| `LoginPanel.java` | `handleLogin()` | Calls `DataManager.getInstance().login()` |
| `DataManager.java` | `login()` | Uses `LoginProxy` internally |

### Code to Show (LoginProxy.java):
```java
public class LoginProxy implements LoginService {
    private final LoginService realLoginService;
    private int failedAttempts = 0;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    
    @Override
    public User login(String username, String password) {
        // PROXY PATTERN - Validation before real service
        if (!validateInput(username, password)) {
            System.out.println("[LoginProxy] Login failed: Invalid input format.");
            return null;
        }
        
        if (!validateUsernameFormat(username)) {
            System.out.println("[LoginProxy] Login failed: Invalid username format.");
            return null;
        }
        
        // Log the attempt
        System.out.println("[LoginProxy] Login attempt for user: " + username);
        
        // Delegate to real service
        User user = realLoginService.login(username, password);
        
        // Track failed attempts for rate limiting
        if (user == null) {
            failedAttempts++;
        } else {
            failedAttempts = 0;
        }
        
        return user;
    }
    
    private boolean validateInput(String username, String password) {
        return username != null && !username.trim().isEmpty() 
                && password != null && !password.trim().isEmpty();
    }
    
    private boolean validateUsernameFormat(String username) {
        return username.matches("^[a-zA-Z0-9_]+$");
    }
}
```

### DataManager Integration:
```java
// DataManager.java - constructor
private DataManager() {
    // ... other initializations ...
    
    // PROXY PATTERN - Wrap real service with proxy
    RealLoginService realLoginService = new RealLoginService(userRepository, sessionManager);
    this.loginService = new LoginProxy(realLoginService);
}

public User login(String username, String password) {
    return loginService.login(username, password);  // Uses proxy!
}
```

---

# 📋 Quick Reference Summary

| Member | Pattern | UI Class | Key Method | Key Code Line |
|--------|---------|----------|------------|---------------|
| 1 | Singleton | All panels | Multiple | `DataManager.getInstance()` |
| 2 | Factory | `LoginPanel` | `handleRegister()` | `UserFactory.createUser(role, ...)` |
| 3 | Builder | `CompanyPanel` | Post button listener | `new JobBuilder().set...().build()` |
| 4 | Strategy | `ApplicantPanel` | `refreshJobList()` | `searchStrategy.search(allJobs, query)` |
| 5 | State | `CompanyPanel` | `moveState()` | `app.next()` |
| 6 | Proxy | `LoginPanel` | `handleLogin()` | `loginService.login(user, pass)` |

---

# 📂 Core Classes Distribution (28 Classes)

## Complete File List by Member

### 👤 Member 1: Singleton Pattern (5 files)

| File | Description |
|------|-------------|
| `DataManager.java` | **Main Singleton** - central access point for all data |
| `SessionManager.java` | Handles login/logout session (SRP - separated from DataManager) |
| `UserRepository.java` | Interface for user storage (DIP) |
| `InMemoryUserRepository.java` | Implementation of UserRepository |
| `User.java` | Abstract base class for all users |

---

### 👤 Member 2: Factory Pattern (4 files)

| File | Description |
|------|-------------|
| `UserFactory.java` | **Main Factory** - simple switch-based user creation |
| `User.java` | Abstract product (shared with Member 1) |
| `Applicant.java` | Concrete product - job seeker with resume and saved jobs |
| `Company.java` | Concrete product - employer with company name |

---

### 👤 Member 3: Builder Pattern (5 files)

| File | Description |
|------|-------------|
| `JobBuilder.java` | **Main Builder** - constructs Job step-by-step |
| `Job.java` | The complex object being built |
| `JobRepository.java` | Interface for job storage (DIP) |
| `InMemoryJobRepository.java` | Implementation of JobRepository |
| `JobService.java` | Service layer for job business logic (SRP) |

---

### 👤 Member 4: Strategy Pattern (2 files)

| File | Description |
|------|-------------|
| `SearchStrategy.java` | **Strategy Interface** - defines search contract |
| `KeywordSearchStrategy.java` | Concrete strategy - filters by keyword in title/description |

---

### 👤 Member 5: State Pattern (9 files)

| File | Description |
|------|-------------|
| `ApplicationState.java` | **State Interface** - defines state behavior |
| `AppliedState.java` | Initial state when application submitted |
| `ReviewedState.java` | State after company reviews application |
| `AcceptedState.java` | Final state - application accepted |
| `RejectedState.java` | Final state - application rejected |
| `JobApplication.java` | Context that holds current state |
| `ApplicationRepository.java` | Interface for application storage (DIP) |
| `InMemoryApplicationRepository.java` | Implementation of ApplicationRepository |
| `ApplicationService.java` | Service layer for application logic (SRP) |

---

### 👤 Member 6: Proxy Pattern (4 files)

| File | Description |
|------|-------------|
| `LoginService.java` | **Proxy Interface** - defines login contract |
| `RealLoginService.java` | Real subject - actual login implementation |
| `LoginProxy.java` | **Proxy** - adds validation, logging, rate limiting |
| `NotificationService.java` | Simple notification service (callback-based) |

---

## 📊 Summary: Classes per Member

| Member | Pattern | Files Count | Core Files |
|--------|---------|-------------|------------|
| 1 | Singleton | 5 | `DataManager`, `SessionManager`, `UserRepository`, `InMemoryUserRepository`, `User` |
| 2 | Factory | 4 | `UserFactory`, `User`, `Applicant`, `Company` |
| 3 | Builder | 5 | `JobBuilder`, `Job`, `JobRepository`, `InMemoryJobRepository`, `JobService` |
| 4 | Strategy | 2 | `SearchStrategy`, `KeywordSearchStrategy` |
| 5 | State | 9 | `ApplicationState`, `AppliedState`, `ReviewedState`, `AcceptedState`, `RejectedState`, `JobApplication`, `ApplicationRepository`, `InMemoryApplicationRepository`, `ApplicationService` |
| 6 | Proxy | 4 | `LoginService`, `RealLoginService`, `LoginProxy`, `NotificationService` |

**Total: 28 unique core classes** (User.java is shared between Members 1 and 2)
