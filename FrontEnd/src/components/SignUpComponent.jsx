import GenericFormComponent from "./GenericFormComponent";
import {signup}  from "../api/auth";
import { useNavigate } from "react-router";
export default function SignUpComponent() {
    const navigate= useNavigate()

    const handlSignUp = async (formData) => {
    try {
      const response = await signup(formData);
  
      localStorage.setItem("token", response.token);
      console.log("Login success:", response);
      navigate("/dashboard")
      
    } catch (error) {
      console.error("Login failed", error);
    }
  };
  const formConfig = {
    meta: {
      title: "Complete Payment",
      subtitle: "Confirm your parking reservation",
    },

    fields: [
      {
        name: "username",
        label: "Username",
        type: "text",
        required: true,
        placeholder: "Enter your username",
      },
      {
        name: "email",
        label: "Email",
        type: "email",
        required: true,
        placeholder: "Enter your email",
      },
      {
        name: "password",
        label: "Password",
        type: "password",
        required: true,
        placeholder: "••••••••",
      },
    ],

    actions: {
      submit: {
        show: true,
        text: "Sign Up",
        variant: "contained",
        style: "primary",
        type: "submit",
        onSubmit:handlSignUp
      },
    },
  };


  
  return (<GenericFormComponent config={formConfig} />);
}
