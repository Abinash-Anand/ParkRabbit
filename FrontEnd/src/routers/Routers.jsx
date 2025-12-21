import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
} from "react-router";
import {
  HomeComponent,
  LoginComponent,
  Sidebar,
  SignUpComponent,
} from "../imports/Imports.js";
import ParkingComponent from "../components/ParkingComponent.jsx";
import BillingComponent from "../components/BillingComponent.jsx";
import AccountComponent from "../components/AccountComponent.jsx";
import NotificationComponent from "../components/NotificationComponent.jsx";
import ProtectedRoute from './ProtectedRoute'

const router = createBrowserRouter(
  createRoutesFromElements(
    // public routes
    <>
      <Route path="/" index element={<HomeComponent />} />
      <Route path="/signup" element={<SignUpComponent />} />
      <Route path="/login" element={<LoginComponent />} />
      {/* Protected Routes */}
      <Route element={<ProtectedRoute />}>
        <Route path="/dashboard" element={<Sidebar />}>
          <Route path="parking" element={<ParkingComponent />}></Route>
          <Route path="billing" element={<BillingComponent />}></Route>
          <Route
            path="notification"
            element={<NotificationComponent />}
          ></Route>
          <Route path="account" element={<AccountComponent />}></Route>
        </Route>
      </Route>
    </>
  )
);

export default router;
