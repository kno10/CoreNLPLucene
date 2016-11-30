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

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.util.CoreMap;

/**
 * Class to use CoreNLP as text analyzer in Lucene / Solr.
 * 
 * @author Erich Schubert
 */
public class CoreNLPTokenStream extends TokenStream {
  AnnotationPipeline pipeline;

  CharTermAttribute termAt;

  TypeAttribute typeAt;

  OffsetAttribute offsetAt;

  PositionIncrementAttribute positionAt;

  ArrayList<String> strings = new ArrayList<>();

  Iterator<String> fragments = null;

  Iterator<CoreMap> sentences = null;

  Iterator<CoreLabel> tokens = null;

  int skippedTokens, fragmentsLength, currentFragmentLength;

  public CoreNLPTokenStream(AnnotationPipeline pipeline) {
    this.pipeline = pipeline;
    this.termAt = addAttribute(CharTermAttribute.class);
    this.typeAt = addAttribute(TypeAttribute.class);
    this.offsetAt = addAttribute(OffsetAttribute.class);
    this.positionAt = addAttribute(PositionIncrementAttribute.class);
    reinit();
  }

  public void reinit() {
    strings.clear();
    fragments = null;
    sentences = null;
    tokens = null;
    skippedTokens = -SENTENCE_GAP - FRAGMENT_GAP;
    fragmentsLength = 0;
    currentFragmentLength = 0;
  }

  final static int SENTENCE_GAP = 10, FRAGMENT_GAP = 2;

  @Override
  public final boolean incrementToken() {
    clearAttributes();
    while(tokens == null || !tokens.hasNext())
      if(!getNextSentence())
        return false;
    CoreLabel token = tokens.next();
    // Use the lemmatized word:
    String word = token.get(LemmaAnnotation.class);
    if(word == null) { // Fallback if lemmatization is not enabled.
      token.get(TextAnnotation.class);
    }
    termAt.setLength(0);
    termAt.append(word);
    // Part of speech annotation
    String pos = token.get(PartOfSpeechAnnotation.class);
    typeAt.setType(pos != null ? pos : TypeAttribute.DEFAULT_TYPE);
    // Token character offsets
    int be = token.get(CharacterOffsetBeginAnnotation.class).intValue() + fragmentsLength;
    int en = token.get(CharacterOffsetEndAnnotation.class).intValue() + fragmentsLength;
    offsetAt.setOffset(be, en);
    // Token in-document position increment:
    positionAt.setPositionIncrement(1 + skippedTokens);
    skippedTokens = 0;
    return true;
  }

  private boolean getNextSentence() {
    while(sentences == null || !sentences.hasNext())
      if(!getNextFragment())
        return false;
    tokens = sentences.next().get(TokensAnnotation.class).iterator();
    skippedTokens += SENTENCE_GAP;
    return true;
  }

  private boolean getNextFragment() {
    if(fragments == null)
      fragments = strings.iterator();
    if(!fragments.hasNext())
      return false;
    fragmentsLength += currentFragmentLength;
    String currentFragment = fragments.next();
    currentFragmentLength = currentFragment.length();
    Annotation annotation = new Annotation(currentFragment);
    pipeline.annotate(annotation);
    sentences = annotation.get(SentencesAnnotation.class).iterator();
    skippedTokens += FRAGMENT_GAP;
    return true;
  }

  public void addText(String text) {
    strings.add(text);
  }
}
