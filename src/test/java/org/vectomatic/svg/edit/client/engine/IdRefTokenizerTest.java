/**********************************************
 * Copyright (C) 2011 Lukas Laag
 * This file is part of svgreal.
 * 
 * svgreal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * svgreal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with svgreal.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edit.client.engine;

import junit.framework.TestCase;

import org.vectomatic.svg.edit.client.engine.IdRefTokenizer.IdRefToken;

public class IdRefTokenizerTest extends TestCase {
	public void testTokenizeUrl() {
		IdRefTokenizer tokenizer = new IdRefTokenizer();
		tokenizer.tokenize("url(#foobar)");
		IdRefToken token = tokenizer.nextToken();
		assertNotNull(token);
		assertEquals(IdRefToken.DATA, token.getKind());
		assertEquals("url(#", token.getValue());

		token = tokenizer.nextToken();
		assertNotNull(token);
		assertEquals(IdRefToken.IDREF, token.getKind());
		assertEquals("foobar", token.getValue());

		token = tokenizer.nextToken();
		assertNotNull(token);
		assertEquals(IdRefToken.DATA, token.getKind());
		assertEquals(")", token.getValue());

		token = tokenizer.nextToken();
		assertNull(token);
	}

	public void testTokenizeNone() {
		IdRefTokenizer tokenizer = new IdRefTokenizer();
		tokenizer.tokenize("none");
		IdRefToken token = tokenizer.nextToken();
		assertNotNull(token);
		assertEquals(IdRefToken.DATA, token.getKind());
		assertEquals("none", token.getValue());
		token = tokenizer.nextToken();
		assertNull(token);
	}
}
