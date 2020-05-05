#!/usr/bin/env perl 
#===============================================================================
#
#         FILE: reset_db.pl
#
#        USAGE: ./reset_db.pl  
#
#  DESCRIPTION: 
#
#      OPTIONS: ---
# REQUIREMENTS: ---
#         BUGS: ---
#        NOTES: ---
#       AUTHOR: YOUR NAME (), 
# ORGANIZATION: 
#      VERSION: 1.0
#      CREATED: 05/05/2020 16:05:53
#     REVISION: ---
#===============================================================================

use strict;
use warnings;
use utf8;
use Path::Tiny qw(path);
use MongoDB;


#main
my $dbpass = path("secret.json")->slurp_utf8;
chomp $dbpass;
my $uri = sprintf("mongodb+srv://%s:%s\@cluster0-ta3pc.gcp.mongodb.net/%s?retryWrites=true&w=majority","nailbiter",$dbpass,"beerbot");
my $client = MongoDB->connect($uri);
$client->ns("beerbot.data")->delete_one({id=>"340880765"});
