package geometry;

import tools.GoogleTools;

public abstract class Vector {
	double a, b, c;
	
	public static class CartesianVector extends Vector{		
		public CartesianVector(double a, double b, double c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}	
		
		public CartesianVector crossProduct(CartesianVector other) {
			a = this.b * other.c - this.c * other.b;
			b = this.c * other.a - this.a * other.c;
			c = this.a * other.b - this.b * other.a;

			return new CartesianVector(a, b, c);
		}
		
	}
	
	public static class GeoVector{
		double r, ϕ, θ;
		
		public GeoVector(double r, double φ, double θ) {
			super();
			this.r = r;
			this.ϕ = φ;
			this.θ = θ;
		}
		
		public GeoVector crossProduct(GeoVector other) {
			int r2 = GoogleTools.RADIUS;
			double ϕ1 = this.ϕ;
			double θ1 = this.θ;
			double ϕ2 = other.ϕ;
			double θ2 = other.θ;
			
			double a = r2 * r2 * sin(ϕ1) * sin(θ1) * cos(θ2) - cos(θ1) * sin(θ2) * sin(ϕ2);
			double b = r2 * r2 * (sin(ϕ2) - sin(ϕ1)) * cos(θ1) * cos(θ2);
			double c = r2 * r2 * sin(ϕ1) * sin(ϕ2) * sin(θ2 - θ1);
			
			double r = Math.sqrt(a*a + b*b + c*c);
			double ϕ = Math.acos(Math.toRadians(c / r));
			double θ = Math.atan(Math.toRadians(b / a));

			return new GeoVector(r, ϕ, ϕ);
		}
		
		public double dotProduct(GeoVector other) {
			//return this.a * other.a + this.b * other.b + this.c * other.c;
			int r2 = GoogleTools.RADIUS;
			double ϕ1 = this.ϕ;
			double θ1 = this.θ;
			double ϕ2 = other.ϕ;
			double θ2 = other.θ;
			return r2 * r2 * (sin(ϕ1) * sin(ϕ2) * cos(θ1-θ2) + cos(ϕ1) * cos(ϕ2));
		}

		@Override
		public String toString() {
			return "GeoVector [r=" + r + ", ϕ=" + ϕ + ", θ=" + θ + "]";
		}
	}
	
	public double dotProduct(CartesianVector other) {
		return this.a * other.a + this.b * other.b + this.c * other.c;
	}
	
	public boolean isZeroVector() {
		return getLength() == 0;
	}
	
	public double getLength(){
		return Math.sqrt(a*a + b*b + c*c);
	}
	
	public static double cos(double degree){
		return Math.cos(Point.toRad(degree));
	}
	
	public static double sin(double degree){
		return Math.sin(Point.toRad(degree));
	}

	@Override
	public String toString() {
		return "Vector [a=" + a + ", b=" + b + ", c=" + c + "]";
	}	
	
}
