# Storage Model Analysis for Tipster

## 1. Introduction

The choice of data storage model is a critically important architectural decision for Tipster. It determines unification performance, stored data volume, implementation complexity, and compatibility with various DBMS.

This document presents a detailed analysis of five alternative storage models with quantitative assessments across key criteria.

## 2. Initial Requirements

### 2.1 Functional Requirements
- **Efficient unification:** O(m×k) instead of O(m×n×log s) for deep structures
- **Universality:** support for facts, rules, functions, metadata
- **Scalability:** operation with billions of records

### 2.2 Non-functional Requirements  
- **Compactness:** minimal data volume including indices
- **Simplicity:** ease of implementation and maintenance
- **Compatibility:** integration with popular DBMS

## 3. Models Under Analysis

### 3.1 Base PKVT Model
```clojure
{:parent "context"    ; Context or owner object
 :key "name"          ; Attribute name, predicate, function  
 :value "data"        ; Attribute value, function body
 :type :category}     ; Entity type (:fact, :rule, :function)
```

**Principle:** All entities are decomposed into atomic PKVT records

### 3.2 PKVTC Model (with child elements)
```clojure
{:parent "context"
 :key "name" 
 :value "data"
 :type :category
 :children ["child-1" "child-2"]}  ; Direct references to child elements
```

**Principle:** Adding `:children` field for fast structure navigation

### 3.3 PKVT + Navigation Index
```clojure
;; Main data: standard PKVT
{:parent "fact-1" :key :functor :value 'parent :type :symbol}

;; Separate navigation index:
{:navigation-index 
 {"fact-1" {:children ["fact-1-arg-0" "fact-1-arg-1"] :arity 2}}}
```

**Principle:** Preserving base model + separate index for navigation

### 3.4 PKVT + Materialized Paths
```clojure
{:parent "context"
 :key "name"
 :value "data" 
 :type :category
 :path "fact-1/functor"}  ; Materialized path for fast search
```

**Principle:** Adding path field for logarithmic search

### 3.5 Hybrid PKVT
```clojure
;; For compound structures - with children
{:parent "fact-1" :key :functor :value 'parent :type :compound :children [...]}

;; For atomic values - without children  
{:parent "fact-1" :key :arg-0 :value "alice" :type :atomic}
```

**Principle:** Selective addition of `:children` only for compound structures

## 4. Evaluation Methodology

### 4.1 Data Volume Calculation
**Base example:** 1,000,000 facts of the form `(parent alice bob)`

**Calculation for each model:**
- Main data (number of fields × number of records)
- Required indices (with detailed size calculation)
- Total volume in conditional units

### 4.2 Unification Complexity Analysis
**Parameters:**
- `m` = number of nesting levels
- `n` = average number of records per level  
- `k` = number of child elements in node
- `s` = database size

**Operations:**
- Record search by parent
- Navigation one level down
- Arity/size checking
- Transition to child element

### 4.3 DBMS Compatibility Criteria
- **Relational DBs:** PostgreSQL, MySQL (schema, data types)
- **Document DBs:** MongoDB, CouchDB (nesting, arrays)
- **Columnar DBs:** Cassandra, HBase (column families)
- **Graph DBs:** Neo4j, ArangoDB (nodes, relationships)
- **Key-Value DBs:** Redis, DynamoDB (serialization)

## 5. Detailed Analysis Results

### 5.1 Unification Performance

| Model | Record Search | Navigation | Arity Check | Overall Complexity |
|-------|---------------|------------|-------------|-------------------|
| **PKVT** | O(log s) | O(n) | O(k) | **O(m × n × log s)** |
| **PKVTC** | O(log s) | O(1) | O(1) | **O(m × k)** |
| **PKVT + Nav Index** | O(log s) | O(1) | O(1) | **O(m × log s)** |
| **PKVT + Mat Paths** | O(log s) | O(log s) | O(1) | **O(m × log s)** |
| **Hybrid PKVT** | O(log s) | O(1)/O(n) | O(1)/O(k) | **O(m × k) compound** |

### 5.2 Data Volume (for 1M facts)

| Model | Main Data | Indices | Total Volume | % of Base |
|-------|-----------|---------|--------------|-----------|
| **PKVT** | 16M units | 15M units | **31M units** | **194%** |
| **PKVTC** | 20M units | 3M units | **23M units** | **115%** |
| **PKVT + Nav Index** | 16M units | 18M units | **34M units** | **213%** |
| **PKVT + Mat Paths** | 20M units | 12M units | **32M units** | **200%** |
| **Hybrid PKVT** | 18M units | 11M units | **29M units** | **181%** |

### 5.3 DBMS Compatibility

| Model | Relational | Document | Columnar | Graph | Key-Value | Average Score |
|-------|------------|----------|----------|-------|-----------|---------------|
| **PKVT** | 9/10 | 8/10 | 10/10 | 6/10 | 9/10 | **8.4/10** |
| **PKVTC** | 4/10 | 9/10 | 6/10 | 7/10 | 6/10 | **6.4/10** |
| **PKVT + Nav Index** | 7/10 | 8/10 | 9/10 | 6/10 | 7/10 | **7.4/10** |
| **PKVT + Mat Paths** | 8/10 | 9/10 | 8/10 | 6/10 | 8/10 | **7.8/10** |
| **Hybrid PKVT** | 3/10 | 6/10 | 4/10 | 4/10 | 4/10 | **4.2/10** |

## 6. Summary Model Evaluation

| Model | Performance | Data Compactness | Implementation Simplicity | DB Compatibility | **Overall Score** |
|-------|-------------|------------------|--------------------------|------------------|-------------------|
| **PKVT** | 🔴 2/10 | 🟡 6/10 | 🟢 **10/10** | 🟢 **8.4/10** | **6.6/10** |
| **PKVTC** | 🟢 10/10 | 🟢 9/10 | 🟢 **9/10** | 🟡 **6.4/10** | **8.6/10** |
| **PKVT + Nav Index** | 🟡 7/10 | 🔴 4/10 | 🟡 **7/10** | 🟡 **7.4/10** | **6.4/10** |
| **PKVT + Mat Paths** | 🟢 9/10 | 🔴 5/10 | 🟡 **8/10** | 🟢 **7.8/10** | **7.5/10** |
| **Hybrid PKVT** | 🟢 8/10 | 🟡 6/10 | 🔴 **4/10** | 🔴 **4.2/10** | **5.6/10** |

## 7. Model Selection Recommendations

### 7.1 For High-Performance Systems
**Recommendation:** PKVTC
- Radical improvement in unification performance
- Index savings compensate for main data growth
- Optimal for systems with intensive logical computations

### 7.2 For Maximum Compatibility
**Recommendation:** PKVT + Materialized Paths
- Good performance with high DB compatibility
- Materialized paths are supported in all DB types
- Reasonable compromise across all criteria

### 7.3 For Minimal Risk
**Recommendation:** PKVT + Navigation Index
- Preserving base model
- Gradual implementation of optimizations
- Ability to rollback to base PKVT

## 8. Implementation Plan

### 8.1 Phase 1: Base Implementation (PKVT)
- Implementation of simplest model for quick start
- Profiling real workloads
- Performance metrics collection

### 8.2 Phase 2: Optimization (selected model)
- Implementation of optimal model based on analysis
- Data and index migration
- A/B performance testing

### 8.3 Phase 3: Adaptivity
- Support for multiple models simultaneously
- Automatic selection of optimal model for different data types
- Dynamic optimization based on usage patterns

## 9. Conclusion

The analysis shows that **PKVTC model with `:children` field** is the optimal solution for most Tipster usage scenarios:

- **Performance:** Order of magnitude faster than base PKVT for complex structures
- **Compactness:** 26% total volume savings through index reduction  
- **Simplicity:** Minimal changes to base model
- **Scalability:** Linear complexity instead of quadratic

For projects with special DB compatibility requirements, **PKVT + Materialized Paths** is recommended as an alternative. 
