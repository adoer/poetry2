## ADDED Requirements

### Requirement: Admin can list quotes
The system SHALL provide a paginated quote list view for admin users.

#### Scenario: View quote list
- **WHEN** admin accesses the quote management page
- **THEN** system displays a paginated table with columns: ID, content (truncated), poetryName, writer
- **THEN** each row has edit and delete action buttons

#### Scenario: Search quotes
- **WHEN** admin types a keyword in the search box
- **THEN** system filters quotes by content or writer matching the keyword

### Requirement: Admin can create a quote
The system SHALL allow admin to create new quotes with auto-assigned ID.

#### Scenario: Create new quote
- **WHEN** admin fills in the create form and submits
- **THEN** system assigns the next available ID (max(id) + 1) and creates the quote record
- **THEN** system returns success and refreshes the list

### Requirement: Admin can edit a quote
The system SHALL allow admin to update existing quote fields.

#### Scenario: Edit existing quote
- **WHEN** admin modifies fields and submits the edit form
- **THEN** system updates the quote record and returns success

### Requirement: Admin can delete a quote
The system SHALL allow admin to delete a quote with confirmation.

#### Scenario: Delete quote
- **WHEN** admin clicks delete and confirms in the popconfirm dialog
- **THEN** system deletes the quote record
- **THEN** system refreshes the list
