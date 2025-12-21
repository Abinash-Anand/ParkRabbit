// auth.service.js
export const logout = () => {
  localStorage.removeItem("token");
  console.log("Logout");
};
