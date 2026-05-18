package com.kadoozin.encurtador_url.database.repository;

import com.kadoozin.encurtador_url.database.entities.Url;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlRepository extends MongoRepository<Url, String> {
}
