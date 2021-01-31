#!/usr/bin/env python
""" generated source for module Cache """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.util
import java.util.Collections

import java.util.Date

import java.util.HashMap

import java.util.Map

import org.apache.commons.lang3.tuple_.ImmutablePair

import org.apache.logging.log4j.Logger

import org.apache.logging.log4j.LogManager

# 
#  * @author Alex Leontiev
#  
class Cache(object):
    """ generated source for class Cache """
    _DATA = Collections.synchronizedMap(HashMap())
    _expirationSec = int()

    def __init__(self, expirationSec):
        """ generated source for method __init__ """
        self._expirationSec = expirationSec

    def get(self, key):
        """ generated source for method get """
        now = Date()
        if self._DATA.containsKey(key) and (now.getTime() - self._DATA.get(key).left.getTime()) / 1000 < self._expirationSec:
            self._Log.info("cache hit")
            return self._DATA.get(key).right
        else:
            self._Log.info("cache miss")
            return None

    def put(self, key, val):
        """ generated source for method put """
        self._DATA.put(key, ImmutablePair(Date(), val))
        return self

Cache._Log = LogManager.getLogger(Cache.__class__)

