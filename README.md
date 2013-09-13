ZanaAfrica Surveys
==================

Architecture
Tiers: Web Server + HTML5 Browser
Hosting Platform: Google App Engine
Language: Java + Facelets + Javascript
Database: Google Datastore + Objectify

Requirements
Mobile/Tablet Web UI for Enumerators
  -- administer/save surveys even while offline
  -- appropriate UI for mobile or tablet devices
  -- multilingual support; multiple simultaneous languages
  -- survey form error checking
  -- lookup and edit existing/incomplete surveys
Administration Web UI for Enumerators
  -- create questions and surveys
  -- handle complex question types (tables, groups, etc.)
  -- dashboard to track enumerator performance
  -- export aggregate data for processing in Excel
  -- authorize admins/enumerators

Phasing
Phase 1 (proof of concept)
  -- create new survey (basic question types)
  -- administer survey and collect data
Phase 2 (general functionality)
  -- question groups and complex question types
  -- add new questions to existing surveys
  -- full offline storage/sync capabilities
Phase 3 (advanced functionality)
  -- user administration
  -- multilingual support
  -- export data for processing
  -- dashboard to track enumerator performance

Content Categorization

Question Categories
 1. identity
 2. demographics
 3. product

Question Group Types
 1. group together only
 2. same question text, different qualifiers, group together
 3. different questions, multi answer, tabular display
 4. rank the following

Question Types
 1. multiple choice (includes yes/no, true/false)
 2. numeric/range
 3. free text (non-aggregate)
 4. combination

