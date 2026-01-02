import httpClient from "./httpClient"

export const getUserReservation = async()=>{
    return httpClient.get('/api/reservations/me')
}