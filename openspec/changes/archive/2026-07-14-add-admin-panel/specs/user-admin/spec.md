## ADDED Requirements

### Requirement: Admin can list users
The system SHALL provide a paginated user list view for admin users (read-only).

#### Scenario: View user list
- **WHEN** admin accesses the user management page
- **THEN** system displays a paginated table with columns: username, nickname, phone, email, vipLevel, createTime, lastlogintime
- **THEN** each row has a detail view button

#### Scenario: Search users
- **WHEN** admin types a keyword in the search box
- **THEN** system filters users by username, nickname, phone, or email matching the keyword

### Requirement: Admin can view user detail
The system SHALL display full user information (excluding password).

#### Scenario: View user detail
- **WHEN** admin clicks the detail button for a user
- **THEN** system shows all user fields except password
