## ADDED Requirements

### Requirement: Admin can list authors
The system SHALL provide a paginated author list view for admin users.

#### Scenario: View author list
- **WHEN** admin accesses the author management page
- **THEN** system displays a paginated table with columns: ID, name, dynasty, simpleIntro (truncated)
- **THEN** each row has edit and delete action buttons

#### Scenario: Search authors
- **WHEN** admin types a keyword in the search box
- **THEN** system filters authors by name matching the keyword

### Requirement: Admin can create an author
The system SHALL allow admin to create new authors with auto-assigned ID.

#### Scenario: Create new author
- **WHEN** admin fills in the create form and submits
- **THEN** system assigns the next available ID (max(id) + 1) and creates the author record
- **THEN** system returns success and refreshes the list

### Requirement: Admin can edit an author
The system SHALL allow admin to update existing author fields.

#### Scenario: Edit existing author
- **WHEN** admin modifies fields and submits the edit form
- **THEN** system updates the author record and returns success

### Requirement: Admin can delete an author
The system SHALL allow admin to delete an author with confirmation.

#### Scenario: Delete author
- **WHEN** admin clicks delete and confirms in the popconfirm dialog
- **THEN** system deletes the author record
- **THEN** system refreshes the list
