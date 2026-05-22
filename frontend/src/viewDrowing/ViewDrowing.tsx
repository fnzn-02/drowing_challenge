import { useEffect, useState } from 'react';
import api from 'axios';
import './viewDrowing.css'

interface DrowingList{
    id: number;
}

function ViewDrowing(){

    const [item, setItem] = useState();


    useEffect(() => {
    const getDrowingList = async () => {

      try {
        const response = await api.get('/list');
        setItem(response.data);
      } catch (error) {
        console.error("리스트 조회 실패", error);
      }
    };
    getDrowingList();
  }, []);

    return(
        <>

        </>
    )
}

export default ViewDrowing;