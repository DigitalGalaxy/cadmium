#
# Cookbook Name:: cadmium
# Recipe:: war
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

log "Queuing war initialization:" do
end

# Fetch artifact from maven repository
cadmium_init_war "#{node[:cadmium][:jetty_root]}/#{node[:cadmium][:domain]}" do
	action :fetch
end

# Initialize war from chef cache directory.
cadmium_init_war "#{node[:cadmium][:jetty_root]}/#{node[:cadmium][:domain]}" do
	action :init
end

