/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.opennlp.corpus_server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.opennlp.corpus_server.search.SearchService;
import org.apache.opennlp.corpus_server.store.CorpusStore;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.xml.sax.SAXException;

public class CorpusResource {

  private final CorpusStore corpus;
  private final SearchService service;
	
  public CorpusResource(CorpusStore corpus, SearchService service) {
    this.corpus = corpus;
    this.service = service;
  }
	
	/**
	 * Adds a new CAS to the store.
	 * 
	 */
	// TODO: Should fail if resource already exists.
	@POST
	@Consumes(MediaType.TEXT_XML)
	@Path("{casId}")
	public void addCAS(@PathParam("casId") String casId, 
			byte[] cas) throws IOException {
		corpus.addCAS(casId, cas);
	}
	
	/**
	 * Updates an existing CAS in the store.
	 */
	// TODO: Should fail is resource does not exist
	@PUT
	@Consumes(MediaType.TEXT_XML)
	@Path("{casId}")
	public void updateCAS(@PathParam("casId") String casId, 
			byte[] cas) throws IOException {
		corpus.updateCAS(casId, cas);
	}
	
	/**
	 * Retrieves an existing CAS form the store.
	 * @param casId
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("{casId}")
	public byte[] getCAS(@PathParam("casId") String casId) throws IOException{
		return corpus.getCAS(casId);
	}

	/**
	 * Retrieves the type system for this corpus.
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("_typesystem")
	public byte[] getTypeSystem() throws IOException {
		TypeSystemDescription typeSystem = corpus.getTypeSystem();

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		
		try {
			typeSystem.toXML(bytes);
		} catch (SAXException e) {
		  throw new IOException(e);
		} catch (IOException e) {
			throw new IllegalStateException("Writing to memory must not fail!");
		}
		
		return bytes.toByteArray();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("_search")
	public List<String> search(@QueryParam("q") String q) throws IOException {
	  return service.search(corpus, q);
	}
}
