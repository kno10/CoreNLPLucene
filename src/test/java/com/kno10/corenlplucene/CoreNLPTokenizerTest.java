package com.kno10.corenlplucene;

/*
 * Copyright (C) 2016
 * Erich Schubert
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.junit.Test;

import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.PropertiesUtils;

public class CoreNLPTokenizerTest extends BaseTokenStreamTestCase {
  /** Test splitting only */
  @Test
  public void testBasic() throws IOException {
    AnnotationPipeline pipeline = new StanfordCoreNLP(PropertiesUtils.asProperties(//
        "annotators", "tokenize,ssplit", //
        "tokenize.language", "en", //
        "tokenize.options", "americanize=true,asciiQuotes=true,ptb3Dashes=true,ptb3Ellipsis=true,untokenizable=noneKeep" //
    ));

    CoreNLPTokenizer tokenizer = new CoreNLPTokenizer(pipeline);
    String str = "Mary had a little lamb. And everywhere that Mary went, the lamb was sure to go.";
    tokenizer.setReader(new StringReader(str));
    assertTokenStreamContents(tokenizer, //
        new String[] { "Mary", "had", "a", "little", "lamb", ".", //
            "And", "everywhere", "that", "Mary", "went", ",", //
            "the", "lamb", "was", "sure", "to", "go", "." },
        // Start offsets:
        new int[] { 0, 5, 9, 11, 18, 22, //
            24, 28, 39, 44, 49, 53, //
            55, 59, 64, 68, 73, 76, 78 },
        // End offsets:
        new int[] { 4, 8, 10, 17, 22, 23, //
            27, 38, 43, 48, 53, 54, //
            58, 63, 67, 72, 75, 78, 79 },
        // Increments:
        new int[] { 1, 1, 1, 1, 1, 1, //
            1 + CoreNLPTokenizer.SENTENCE_GAP, 1, 1, 1, 1, 1, //
            1, 1, 1, 1, 1, 1, 1, 1 } //
    );
  }

  /** Test with part of speech and lemmatization */
  @Test
  public void testWithLemma() throws IOException {
    AnnotationPipeline pipeline = new StanfordCoreNLP(PropertiesUtils.asProperties(//
        "annotators", "tokenize,ssplit,pos,lemma", //
        "parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz", //
        "tokenize.language", "en", //
        "tokenize.options", "americanize=true,asciiQuotes=true,ptb3Dashes=true,ptb3Ellipsis=true,untokenizable=noneKeep" //
    ));

    CoreNLPTokenizer tokenizer = new CoreNLPTokenizer(pipeline);
    String str = "Mary had a little lamb. And everywhere that Mary went, the lamb was sure to go.";
    tokenizer.setReader(new StringReader(str));
    assertTokenStreamContents(tokenizer, //
        new String[] { "Mary", "have", "a", "little", "lamb", ".", //
            "and", "everywhere", "that", "Mary", "go", ",", //
            "the", "lamb", "be", "sure", "to", "go", "." },
        // Start offsets:
        new int[] { 0, 5, 9, 11, 18, 22, //
            24, 28, 39, 44, 49, 53, //
            55, 59, 64, 68, 73, 76, 78 },
        // End offsets:
        new int[] { 4, 8, 10, 17, 22, 23, //
            27, 38, 43, 48, 53, 54, //
            58, 63, 67, 72, 75, 78, 79 },
        // Types
        new String[] { "NNP", "VBD", "DT", "JJ", "NN", ".", //
            "CC", "RB", "IN", "NNP", "VBD", ",", //
            "DT", "NN", "VBD", "JJ", "TO", "VB", "." },
        // Increments:
        new int[] { 1, 1, 1, 1, 1, 1, //
            1 + CoreNLPTokenizer.SENTENCE_GAP, 1, 1, 1, 1, 1, //
            1, 1, 1, 1, 1, 1, 1, 1 } //
    );
  }

  /** Test with NER */
  @Test
  public void testWithNER() throws IOException {
    AnnotationPipeline pipeline = new StanfordCoreNLP(PropertiesUtils.asProperties(//
        "annotators", "tokenize,ssplit,pos,lemma,ner", //
        "parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz", //
        "tokenize.language", "en", //
        "tokenize.options", "americanize=true,asciiQuotes=true,ptb3Dashes=true,ptb3Ellipsis=true,untokenizable=noneKeep" //
    ));

    CoreNLPTokenizer tokenizer = new CoreNLPTokenizer(pipeline);
    String str = "Mary had a little lamb. And everywhere that Mary went, the lamb was sure to go.";
    tokenizer.setReader(new StringReader(str));
    assertTokenStreamContents(tokenizer, //
        new String[] { "Mary", "have", "a", "little", "lamb", ".", //
            "and", "everywhere", "that", "Mary", "go", ",", //
            "the", "lamb", "be", "sure", "to", "go", "." },
        // Start offsets:
        new int[] { 0, 5, 9, 11, 18, 22, //
            24, 28, 39, 44, 49, 53, //
            55, 59, 64, 68, 73, 76, 78 },
        // End offsets:
        new int[] { 4, 8, 10, 17, 22, 23, //
            27, 38, 43, 48, 53, 54, //
            58, 63, 67, 72, 75, 78, 79 },
        // Types
        new String[] { "PERSON", "VBD", "DT", "JJ", "NN", ".", //
            "CC", "RB", "IN", "PERSON", "VBD", ",", //
            "DT", "NN", "VBD", "JJ", "TO", "VB", "." },
        // Increments:
        new int[] { 1, 1, 1, 1, 1, 1, //
            1 + CoreNLPTokenizer.SENTENCE_GAP, 1, 1, 1, 1, 1, //
            1, 1, 1, 1, 1, 1, 1, 1 } //
    );
  }
}
