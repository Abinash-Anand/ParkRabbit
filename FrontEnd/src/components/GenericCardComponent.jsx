import Card from "@mui/material/Card";
import CardActions from "@mui/material/CardActions";
import CardContent from "@mui/material/CardContent";
import CardMedia from "@mui/material/CardMedia";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import Lottie from "lottie-react";

export default function GenericCardComponent({
  title,
  description,
  buttonText,
  onButtonClick,
  animationData,
}) { 
 
  return (
   
    <Card sx={{ maxWidth: 345 }}>
      <CardMedia>
        <Lottie animationData={animationData} loop autoplay />
      </CardMedia>
      <CardContent>
        <Typography gutterBottom variant="h5" component="div">
         {title}
        </Typography>
        <Typography variant="body2" sx={{ color: "text.secondary" }}>
          {description}
        </Typography>
      </CardContent>
      <CardActions>
        <Button size="small" onClick={onButtonClick}>{buttonText}</Button>
      </CardActions>
    </Card>
  );
}
