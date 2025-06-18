# Tipster Documentation Structure and Organization

## Document Purpose

This document defines the principles for organizing Tipster project documentation and contains rules for placing materials in folders to maintain logical structure and navigation convenience.

## Documentation Folder Structure

```
docs/
├── en/                        # English documentation (primary)
│   ├── general/              # General information
│   ├── concepts/             # Conceptual materials
│   ├── terms/                # Technical specifications
│   ├── DOCUMENTATION_STRUCTURE.md  # This document
│   └── README.md                   # Navigation guide
├── ru/                        # Russian documentation (secondary)
│   ├── general/              # Общая информация
│   ├── concepts/             # Концептуальные материалы
│   ├── terms/                # Технические спецификации
│   ├── DOCUMENTATION_STRUCTURE.md  # Russian version
│   └── README.md                   # Russian navigation guide
└── [other-lang]/              # Future language additions
    ├── general/
    ├── concepts/
    ├── terms/
    ├── DOCUMENTATION_STRUCTURE.md
    └── README.md
```

## Folder Purpose and Content

### 📁 `docs/en/general/` - General Information
**Target Audience:** Wide range of users, new project participants, managers

**Content Type:**
- High-level system descriptions
- Project introduction and philosophy
- Strategic planning
- Architectural diagrams and overviews

**Example Documents:**
- `architecture.md` - System architecture with components
- `general_description.md` - General language description and comparisons
- `roadmap.md` - Development roadmap

**Placement Criteria:**
- ✅ Document is understandable without deep technical knowledge
- ✅ Contains general overview or introduction
- ✅ Focused on strategic level
- ❌ Does not contain detailed technical specifications

### 📁 `docs/en/concepts/` - Conceptual Materials  
**Target Audience:** Developers, architects, researchers

**Content Type:**
- Fundamental system concepts
- Architectural decision rationales
- Design philosophy
- Theoretical foundations

**Example Documents:**
- `core.md` - Key concepts: terms, semantics
- `core-design-rationale.md` - Design decision rationales

**Placement Criteria:**
- ✅ Explains "why" a particular decision was made
- ✅ Contains conceptual models and principles
- ✅ Focus on ideas, not implementation
- ❌ Is not a technical specification

### 📁 `docs/en/terms/` - Technical Specifications
**Target Audience:** Core developers, implementers

**Content Type:**
- Formal technical specifications
- Detailed algorithms and data structures  
- Performance analysis with metrics
- Functional requirements
- API documentation

**Example Documents:**
- `functional_specification.md` - Core functional requirements
- `DDS.md` - Detailed data structure specification
- `term-decomposition.md` - Term decomposition algorithms
- `storage-model-analysis.md` - Storage model analysis

**Placement Criteria:**
- ✅ Contains concrete algorithms and code examples
- ✅ Includes technical metrics and measurements
- ✅ Defines precise implementation requirements
- ✅ Can be used as technical specifications

## Document Placement Rules

### When Creating a New Document

1. **Identify Target Audience:**
   - General audience → `en/general/` (+ `ru/general/`)
   - Developers (concepts) → `en/concepts/` (+ `ru/concepts/`)  
   - Developers (implementation) → `en/terms/` (+ `ru/terms/`)

2. **Assess Content Type:**
   - Overview/strategic → `en/general/` (+ `ru/general/`)
   - Conceptual/philosophical → `en/concepts/` (+ `ru/concepts/`)
   - Technical/algorithmic → `en/terms/` (+ `ru/terms/`)

3. **Check Criteria:**
   - Uses formulas and algorithms? → `en/terms/` (+ `ru/terms/`)
   - Explains "why" without technical details? → `en/concepts/` (+ `ru/concepts/`)
   - Provides general overview? → `en/general/` (+ `ru/general/`)

### When Uncertain About Placement

**Self-Check Questions:**
- Can a manager understand the main idea of the document? → `en/general/` (+ `ru/general/`)
- Are technical knowledge required for understanding? → `en/concepts/` or `en/terms/` (+ Russian versions)
- Can you write code based on the document? → `en/terms/` (+ `ru/terms/`)
- Does the document explain architectural decisions? → `en/concepts/` (+ `ru/concepts/`)

## Language Versions

### Language Standards
- **Primary Language: English** - Common project language for all contributors
- **Secondary Language: Russian** - Additional language support  
- **Policy:** English documentation is the primary project standard
- **Completeness:** Each document should have both language versions when possible

### Translation Principles
- English documentation serves as the project standard
- All contributors ensure high-quality English versions
- Individual developers may work in their preferred languages first
- Translations to English must be extremely precise and accurate
- Other language versions translate from English as the authoritative base
- Each language has identical file structure: `[lang]/{general,concepts,terms}/`
- Navigation and organizational documents exist in each language folder

### File Naming
- File names must be identical across all language versions: `[lang]/category/file.md`
- Use kebab-case for file names
- Avoid national characters in file names
- Each language folder contains identical structure and file names

## Change History

### Restructuring 2025-01
**Date:** January 2025  
**Reason:** Mixing of conceptual materials with technical specifications

**Performed Moves:**
- `concepts/*/term-decomposition.md` → `terms/*/term-decomposition.md`
- `concepts/*/storage-model-analysis.md` → `terms/*/storage-model-analysis.md`

**Move Rationales:**
- `term-decomposition.md`: 1127 lines of technical algorithms and data structures  
- `storage-model-analysis.md`: Quantitative analysis with performance metrics

### Language Standards Update 2025-01
**Date:** January 2025
**Reason:** Clarification of multilingual project standards

**Changes:**
- Confirmed English as primary project language (common standard)
- Established precise translation requirements for individual contributors
- Restructured root documentation for language-based organization
- Clarified translation workflow and quality standards

### Structure Reorganization 2025-01
**Date:** January 2025
**Reason:** More elegant language-first organization

**Changes:**
- Moved from `category/language/` to `language/category/` structure
- English: `docs/en/{general,concepts,terms}/`
- Russian: `docs/ru/{general,concepts,terms}/`
- Pure language-based organization for scalability

### Clean Language Structure 2025-01
**Date:** January 2025
**Reason:** Prepare for multilingual scalability

**Changes:**
- Moved all documents into language folders: `docs/[lang]/`
- Removed files from root `docs/` folder
- Each language has identical structure and organizational documents
- Designed for easy addition of new languages with consistent structure

## Structure Monitoring

### Regular Checks
- Monthly audit of new document placement
- Verification of content alignment with folder purpose
- Validation of language version completeness

### Problem Indicators
- 🚨 Technical details in `en/general/` or `ru/general/`
- 🚨 General descriptions in `en/terms/` or `ru/terms/`  
- 🚨 Missing language version (missing `en/category/file.md` ↔ `ru/category/file.md`)
- 🚨 Naming convention violations

---

*Document created: January 2025*  
*Last updated: January 2025* 
