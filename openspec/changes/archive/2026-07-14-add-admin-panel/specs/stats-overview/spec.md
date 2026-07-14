## ADDED Requirements

### Requirement: Admin can view overview statistics
The system SHALL display aggregate counts of all data types.

#### Scenario: View overview cards
- **WHEN** admin accesses the dashboard or stats page
- **THEN** system shows count cards: total poems, total quotes, total authors, total users

### Requirement: Admin can view user registration trend
The system SHALL show user registration counts grouped by day.

#### Scenario: View registration trend
- **WHEN** admin accesses the stats page
- **THEN** system displays a chart of user registrations per day for the last 30 days

### Requirement: Admin can view favorite distribution
The system SHALL show the distribution of favorites by type (poesy vs quotes).

#### Scenario: View favorite breakdown
- **WHEN** admin accesses the stats page
- **THEN** system shows the count of favorites grouped by type
