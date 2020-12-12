# Trackable Grails Plugin [![Build Status](https://travis-ci.com/billgonemad/grails-trackable.svg?branch=master)](https://travis-ci.com/billgonemad/grails-trackable)

Welcome to the  source code for the Trackable Grails Plugin.  

This plugin provides the ability for users to track things such as countries visited, favorite beers, etc.

## Requirements
- Grails 4.0+
- JDK 1.8+
- Spring Security Core Plugin 4.0+

## Installation
`compile 'org.grails.plugins:trackable:0.1.1--SNAPSHOT'`

## Usage

On any domain classes you wish users to be able to track, you'll just need to implement the _Trackable_ trait provited.

`class Country implements Trackable`

