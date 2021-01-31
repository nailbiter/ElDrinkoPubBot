#!/usr/bin/env python
""" generated source for module MongoPersistentStorage """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.mongodb
import com.mongodb.client.MongoCollection

import com.mongodb.client.model.Filters

import com.mongodb.client.model.Updates

import org.bson.Document

import org.bson.conversions.Bson

import org.json.JSONObject

import nl.insomnia247.nailbiter.eldrinkopubbot.util.PersistentStorage

# 
#  * @author Alex Leontiev
#  * assumption: database only gets modified through this object
#  
class MongoPersistentStorage(PersistentStorage):
    """ generated source for class MongoPersistentStorage """
    _mongoCollection = None
    _obj = None
    _key = None
    _val = None

    def __init__(self, mongoCollection, key, val):
        """ generated source for method __init__ """
        super(MongoPersistentStorage, self).__init__()
        System.err.format(" ad964b5521b11fb9 \n")
        self._mongoCollection = mongoCollection
        self._key = key
        self._val = val
        System.err.format(" 3ff43cbd930bd1f1 \n")
        _getObj()
        System.err.format(" 2e878d806817478d \n")

    def _getObj(self):
        """ generated source for method _getObj """
        if self._obj == None:
            System.err.format(" a2a82f031dd60d4a \n")
            System.err.format("doc: %s\n", doc)
            if doc == None:
                doc = Document(self._key, self._val)
                self._mongoCollection.insertOne(doc)
                self._obj = JSONObject()
            else:
                System.err.format("json: %s\n", json)
                self._obj = JSONObject(json)

    def contains(self, key):
        """ generated source for method contains """
        return self._obj.has(key)

    def get(self, key):
        """ generated source for method get """
        return self._obj.getString(key)

    def set(self, key, val):
        """ generated source for method set """
        self._mongoCollection.updateOne(Filters.eq(self._key, self._val), Updates.set(key, val))
        self._obj.put(key, val)
        return self

