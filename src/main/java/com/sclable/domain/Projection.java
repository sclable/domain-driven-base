package com.sclable.domain;

/**
 * Projections are short living InMemory read models of linked and dereferenced domain objects which
 * can help to reduce the mapping code in your domain logic. A projection can be used when the
 * structure of the aggregates/entities makes sense for most of the use-cases but limited scenarios
 * require the objects to be dereferenced. A projection is dereferenced in a dedicated factory which
 * is fetched via a repository.
 */
public class Projection extends DomainModel {}
