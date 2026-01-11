export interface AuthRequest {
  username: string;
  password: string;
}

export interface UserDTO {
  id: number;
  username: string;
}

export interface AuthResponse {
  token: string;
  userDTO: UserDTO;
}
