## ADDED Requirements

### Requirement: Admin can list poems
The system SHALL provide a paginated poem list view for admin users.

#### Scenario: View poem list
- **WHEN** admin accesses the poem management page
- **THEN** system displays a paginated table with columns: ID, title, dynasty, writer, type
- **THEN** each row has edit and delete action buttons

#### Scenario: Search poems
- **WHEN** admin types a keyword in the search box
- **THEN** system filters poems by title, content, or writer matching the keyword

### Requirement: Admin can view poem detail
The system SHALL display full poem details including all fields.

#### Scenario: Open poem detail
- **WHEN** admin clicks a poem row
- **THEN** system shows title, dynasty, writer, content, type, shangxi, translation, remark, audioUrl

### Requirement: Admin can create a poem
The system SHALL allow admin to create new poems with auto-assigned ID.

#### Scenario: Create new poem
- **WHEN** admin fills in the create form and submits
- **THEN** system assigns the next available ID (max(id) + 1) and creates the poem record
- **THEN** system returns success and refreshes the list

### Requirement: Admin can edit a poem
The system SHALL allow admin to update existing poem fields.

#### Scenario: Edit existing poem
- **WHEN** admin modifies fields and submits the edit form
- **THEN** system updates the poem record and returns success

### Requirement: Admin can delete a poem
The system SHALL allow admin to delete a poem with confirmation.

#### Scenario: Delete poem
- **WHEN** admin clicks delete and confirms in the popconfirm dialog
- **THEN** system deletes the poem record
- **THEN** system refreshes the list

### Requirement: Type field uses combined selection and input
The type field SHALL support both selecting from existing categories and typing new ones.

#### Scenario: Select existing category
- **WHEN** admin clicks the type field
- **THEN** system shows a dropdown of all existing categories from /api/category
- **THEN** admin can select multiple categories

#### Scenario: Type new category
- **WHEN** admin types a new category name not in the dropdown
- **THEN** system accepts the input and saves it as part of the JSON array
