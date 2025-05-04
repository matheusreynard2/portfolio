export interface EnderecoGeolocalizacao {
  results: GeocodingResult[];
  status: string;
  error_message?: string
  plus_code?: PlusCode;
}

export interface PlusCode {
  compound_code: string;
  global_code: string;
}

export interface GeocodingResult {
  formatted_address: string;
  place_id: string;
  geometry: Geometry;
  address_components: AddressComponent[];
}

export interface AddressComponent {
  long_name: string;
  short_name: string;
  types: string[];
}

export interface Geometry {
  location: Location;
  location_type: string;
  viewport: Viewport;
}

export interface Location {
  lat: number;
  lng: number;
}

export interface Viewport {
  northeast: Location;
  southwest: Location;
}
