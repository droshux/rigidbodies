package io.github.droshux.rigidbodies;

import java.awt.*;

public class RigidBodyBuilder {
	private String id;
	private float mass;
	private Point position;
	private Color colour;
	private String colliderFile;
	private boolean useGravity;
	private final Canvas canvasTemplate;

	public RigidBodyBuilder(Canvas canvas) {
		this.canvasTemplate = canvas;
	}

	public RigidBodyBuilder setId(String id) {
		this.id = id;
		return this;
	}

	public RigidBodyBuilder setMass(float mass) {
		this.mass = mass;
		return this;
	}

	public RigidBodyBuilder setPosition(Point position) {
		this.position = position;
		return this;
	}

	public RigidBodyBuilder setColour(Color col) {
		this.colour = col;
		return this;
	}

	public RigidBodyBuilder setColliderFile(String colliderFile) {
		this.colliderFile = colliderFile;
		return this;
	}

	public RigidBodyBuilder setGravity(boolean useGravity) {
		this.useGravity = useGravity;
		return this;
	}

	@SuppressWarnings("UnusedReturnValue")
	public RigidBody createRigidBody() {
		return new RigidBody(id, mass, position, colour, colliderFile, canvasTemplate, useGravity);
	}
}