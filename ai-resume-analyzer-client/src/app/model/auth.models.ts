export interface AuthRequest {
  username: string;
  password: string;
}

export interface UserDTO {
  id: number;
  username: string;
  email?: string;
  roles?: string[];
}

export interface AuthResponse {
  token: string;
  user: UserDTO;
}
