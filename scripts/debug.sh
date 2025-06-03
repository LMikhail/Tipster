#!/bin/bash

# Отладочный режим Tipster логического движка с детальным выводом
echo "Tipster Logic Engine Debug Mode"
echo "==============================="

echo ""
echo "Running simple test with detailed output:"
clj -M -e "
(require '[tipster.core :as tipster])
(tipster/reset-tipster!)
(println \"Initial state:\")
(println \"  Knowledge Base:\" @tipster/knowledge-base)
(println \"\nAdding facts...\")
(tipster/deffact (test-fact alice))
(tipster/deffact (parent alice bob))
(println \"  Knowledge Base after facts:\" @tipster/knowledge-base)
(println \"\nAdding rule...\")
(tipster/defrule (grandparent ?X ?Z) [(parent ?X ?Y) (parent ?Y ?Z)])
(println \"  Knowledge Base after rule:\" @tipster/knowledge-base)
(println \"\nQuerying facts...\")
(println \"  Query (test-fact ?X):\" (tipster/query (test-fact ?X)))
(println \"  Query (parent ?X ?Y):\" (tipster/query (parent ?X ?Y)))
(println \"\nDebug complete!\")
" 
