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

import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.PropertiesUtils;

public class CoreNLPTokenizerFactory extends TokenizerFactory {
  AnnotationPipeline pipeline = new StanfordCoreNLP(PropertiesUtils.asProperties(//
      "annotators", "tokenize,ssplit,pos,lemma", //
      "parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz", //
      "tokenize.language", "en", //
      "tokenize.options", "americanize=true,asciiQuotes=true,ptb3Dashes=true,ptb3Ellipsis=true,untokenizable=noneKeep" //
  ));

  public CoreNLPTokenizerFactory(Map<String, String> args) {
    super(args);
  }

  @Override
  public Tokenizer create(AttributeFactory factory) {
    return new CoreNLPTokenizer(factory, pipeline);
  }
}
