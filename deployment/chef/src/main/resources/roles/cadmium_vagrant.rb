#
# Cookbook Name:: cadmium
# Role:: cadmium_vagrant
#
# Copyright 2014, Meltmedia
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

name "cadmium_vagrant"
description "Role to build a jetty vagrant instance."
run_list(%w{
        recipe[apt::default]
        recipe[cadmium::default]
        recipe[cadmium::init]
        recipe[cadmium::ssl]
        recipe[cadmium::install_cli]
        recipe[cadmium::ssh]
        recipe[cadmium::war]
        recipe[cadmium::deploy_jetty]
})
default_attributes(
	:cadmium => {
		:cadmium_user  => "vagrant",
		:cadmium_group => "vagrant",
                :external_http_port => "8080",
                :external_https_port => "8443"
	}
)