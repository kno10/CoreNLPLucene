CoreNLP in Lucene and Solr
==========================

The aim of this project is to integrate [Stanford CoreNLP](http://stanfordnlp.github.io/CoreNLP/)
into [Apache Lucene](http://lucene.apache.org/) to improve search results and allow more complex
data analysis.

TODO
----

This is not a finished "product". Rather, it is a tool I simply needed for a project. Use at your own risk.

* Configuration such as language selection currently requires modifications to the code.

Dependencies
------------

* [Stanford CoreNLP](http://stanfordnlp.github.io/CoreNLP/) (stanford-corenlp)
* [Apache Lucene](http://lucene.apache.org/) (lucene-analyzers-common)

To build, use the Gradle wrapper: `./gradlew jar`

Usage in Solr
-------------

Build everything, and copy all the jars into the classpath.

Then you need to define a field type, e.g.

```
<fieldType name="corenlp_en" class="solr.TextField">
  <analyzer type="index">
    <tokenizer class="com.kno10.corenlplucene.CoreNLPTokenizerFactory"/>
    <filter class="solr.LowerCaseFilterFactory"/>
    <filter class="solr.TypeTokenFilterFactory" types="stoptypes.txt" useWhitelist="false"/>
  </analyzer>
  <analyzer type="query">
    <tokenizer class="com.kno10.corenlplucene.CoreNLPTokenizerFactory"/>
    <filter class="solr.LowerCaseFilterFactory"/>
    <filter class="solr.TypeTokenFilterFactory" types="stoptypes.txt" useWhitelist="false"/>
  </analyzer>
</fieldType>
```
where `stoptypes.txt` contains [Penn treebank token types](http://web.mit.edu/6.863/www/PennTreebankTags.html#Word) that you are not interested in. 

Peculiarities
-------------

In the default configuration, the sentence

`Data Mining analyzes data.`

will yield the lemmatized tokens

`data/NNP mining/NNP analyze/VBZ datum/NNS ./.`

Note that `data` was lemmatized to `datum` (singular), but because `Data Mining` is a named entity, it has not been lemmatized.

This is problematic in particular when parsing queries that are not proper sentences.
However, if you use this analyzer for data analysis rather than traditional text search, this may be beneficial.

License
-------

Because Stanford CoreNLP is GPL licensed, and Lucene is Apache licensed,
the resulting combination will have to obey the terms of the GPL-3+ license.

Unless you are willing to license your code as GPL, do not use this in proprietary systems.
(Or make your money with support and services, and open-source your system, like other companies do.)