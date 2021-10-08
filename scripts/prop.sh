#!/bin/bash

function read_prop {
  grep "${1}" gradle.properties | cut -d'=' -f2
}

read_prop "$1"
