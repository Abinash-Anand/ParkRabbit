import GenericCardComponent from "./GenericCardComponent";
import parkingAnimation from "../assets/parkingAnimation.json";
import parkingReserved from "../assets/parkingReserved.json";
import { useNotifications } from "../notifications/useNotifications";
import { parkingLots, reserveParkingSlot } from "../api/parkingLots.js";
import { useEffect, useState } from "react";
import { formatTime } from "../services/dateTime.service.js";
import { getUserReservation } from "../api/reservations.js";
export default function ParkingComponent() {
  const [parking, setParking] = useState([]);
  const { addNotification } = useNotifications();
  const [reservation, setReservation] = useState(null);
  const [queued, setQueued] = useState(false);
  //Handling The parking lots Api
  const handleParkingLots = async () => {
    try {
      const response = await parkingLots();
      setParking(response);
      console.log(response);
    } catch (error) {
      console.error("Login failed", error);
    }
  };
  //Handling the Create Reservation API
  const handleCreateReservation = async (parkingLotId) => {
    try {
      const response = await reserveParkingSlot(parkingLotId);
      if(response?.reservationId === null){
        setQueued(response?.queued)
        addNotification({
        type: "Waitlisted",
        title: response?.message,
       
      });
      }else{
         setReservation(response);
      addNotification({
        type: "success",
        title: "Reservation Confirmed",
        message: `Slot ${response.slotId} reserved until ${formatTime(
          response.expiresAt
        )}`,
      });
      }      
      console.log("handleCreateReservation: ", response)
     
    } catch (error) {
      console.log(error);
    }
  };

  // GET User Reservation
  const handleGetUserReservation = async () => {
    try {
      const response = await getUserReservation();

      //when reservationId is null execute this
      if (response?.reservationId === null) {
        setQueued(response?.queued);
        setReservation(response?.reservationId);
        
        return;
      }
      //If reservationId exists meaning there is an Active reservation
      else if(response?.reservationId !== null)
      setReservation(response);
      setQueued(response?.queued);
    } catch (error) {
      console.error(error)
      setReservation(null);
      setQueued(false);
    }
  };
  
  useEffect(() => {
    handleGetUserReservation();
  }, []);
  const renderView = () => {
    // 1️ Active reservation
    if (reservation?.reservationId ) {
      return (
        <div className="flex gap-2 justify-baseline flex-wrap">
          <GenericCardComponent
            key={reservation?.reservationId}
            title={`${reservation?.parkingLotAddress} Parking Lot ${reservation?.parkingLotId}`}
            description={`Parking Slot No. • ${
              reservation?.slotId
            } Reserved at • ${formatTime(
              reservation?.reservedAt
            )} Expires at • ${formatTime(reservation?.expiresAt)} `}
            animationData={parkingReserved}
          />
        </div>
      );
    }
    // 2️ Queued state
    if (queued) {
      return (
        <GenericCardComponent
          title="Waiting for a Parking Slot"
          description="You are currently in queue. You will be notified once a slot becomes available."
          // animationData={queueAnimation}
        />
      );
    }
   else if (parking.length > 0 ) {
      return (
        <div className="flex gap-2 justify-baseline flex-wrap">
          {parking.map((lot) => (
            <GenericCardComponent
              key={lot.id}
              title={lot.name}
              description={`Euro ${lot.hourlyRate}/hour • ${lot.freeSlots} Available slots • ${lot.occupiedSlots} Occupied slots • ${lot.reservedSlots} Reserved slots  • ${lot.totalSlots} Total slots`}
              buttonText="Reserve"
              onButtonClick={() => handleCreateReservation(lot.id)}
              animationData={parkingAnimation}
            />
          ))}
        </div>
      );
    } else {
      return (
        <GenericCardComponent
          title="Car Parking"
          description="Use Park Rabbit Parking feature which allows you to park you car in the nearest parking lot."
          buttonText="Park"
          onButtonClick={handleParkingLots}
          animationData={parkingAnimation}
        />
      );
    }
  };
  return renderView();
}
