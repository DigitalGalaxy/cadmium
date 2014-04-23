#
# Cookbook Name:: cadmium
# Resource:: deploy_war
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

actions :jetty, :dir

attribute :war_name,       :kind_of => String
attribute :app_path,       :kind_of => String, :required => true
attribute :cookbook,       :kind_of => String
attribute :template,       :kind_of => String
attribute :owner,          :kind_of => String
attribute :group,          :kind_of => String
attribute :port,           :kind_of => String

def initialize(*args)
  super
  @war_name ||= @name
  @owner ||= "#{node[:cadmium][:system_user]}"
  @group ||= "#{node[:cadmium][:system_group]}"
  @port  ||= "8080"
  @cookbook ||= "cadmium"
  @template ||= "cadmium-jetty.conf.erb"
  action = :jetty
end