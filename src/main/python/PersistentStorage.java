#!/usr/bin/env python
""" generated source for module PersistentStorage """
# package: nl.insomnia247.nailbiter.eldrinkopubbot.util
# 
#  * @author Alex Leontiev
#  
class PersistentStorage(object):
    """ generated source for interface PersistentStorage """
    __metaclass__ = ABCMeta
    @abstractmethod
    def contains(self, key):
        """ generated source for method contains """

    @abstractmethod
    def get(self, key):
        """ generated source for method get """

    @abstractmethod
    def set(self, key, val):
        """ generated source for method set """

