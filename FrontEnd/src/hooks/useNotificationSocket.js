import { useEffect, useRef } from "react";
import { createNotification } from "../notifications/models/notification.model";
import { useNotifications } from "../notifications/useNotifications";

export function useNotificationSocket(userId) {
    console.log("SOCKET CHECKING: ", userId)
    console.log("SOCKET CHECKING URL: ", import.meta.env.VITE_API_WEB_SOCKET)

  const socketRef = useRef(null);
  const { addNotification } = useNotifications();

  useEffect(() => {
    if (!userId) return;

    const socket = new WebSocket(
      `ws://${import.meta.env.VITE_API_WEB_SOCKET}/ws/notifications?userId=${userId}`
    );

    socket.onopen = () => {
      console.log("🔌 WebSocket connected");
    };

    socket.onmessage = (event) => {
      const data = JSON.parse(event.data);

      // Reservation expired
      if (data.reservationId && data.expiredAt) {
        addNotification(
          createNotification({
            type: "warning",
            title: "Reservation Expired",
            message: `${data.parkingLotName} reservation expired`,
          })
        );
      }

      // Slot auto-assigned
      if (data.slotId && data.reservedAt) {
        addNotification(
          createNotification({
            type: "success",
            title: "Slot Assigned",
            message: `Slot assigned at ${data.parkingLotName}`,
          })
        );
      }

      console.log("🔔 Notification received", data);
    };

    socket.onerror = (err) => {
      console.error("❌ WebSocket error", err);
    };

    socket.onclose = () => {
      console.log("🔌 WebSocket disconnected");
    };

    socketRef.current = socket;

    return () => socket.close();
  }, [userId]);
}
