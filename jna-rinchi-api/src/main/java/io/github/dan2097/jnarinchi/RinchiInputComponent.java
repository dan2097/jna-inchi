package io.github.dan2097.jnarinchi;

import io.github.dan2097.jnainchi.InchiInput;

public class RinchiInputComponent extends InchiInput {
	
	private ReactionComponentRole role = ReactionComponentRole.REAGENT;

	public ReactionComponentRole getRole() {
		return role;
	}

	public void setRole(ReactionComponentRole role) {
		this.role = role;
	}
}
