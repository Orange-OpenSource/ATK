/*
 * Software Name : ATK
 *
 * Copyright (C) 2013 France Télécom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------
 *
 * Created     : 12/06/2013
 * Author(s)   : Pierre Crepieux
 */

package com.orange.atk.monitoring;

public class AroSettings {
	private boolean enabled;

	public AroSettings() {
		this.enabled = false;
	}

	public AroSettings(boolean enabled) {
		super();
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
