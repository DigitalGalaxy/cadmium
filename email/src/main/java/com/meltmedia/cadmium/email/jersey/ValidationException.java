/**
 *    Copyright 2012 meltmedia
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.meltmedia.cadmium.email.jersey;

public class ValidationException extends Exception {
	private static final long serialVersionUID = 1L;

	private ValidationError[] errors;

	public ValidationException() {
	    super();
	  }

	  public ValidationException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public ValidationException(String message) {
	    super(message);
	  }

	  public ValidationException(Throwable cause) {
	    super(cause);
	  }

	  public ValidationException(String message, ValidationError[] errors) {
	    super(message);
	    this.errors = errors;
	  }

	  public ValidationError[] getErrors() {
	    return errors;
	  }
}
