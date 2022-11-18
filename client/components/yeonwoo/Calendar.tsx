import FullCalendar from '@fullcalendar/react';
import daygridPlugin from '@fullcalendar/daygrid';

export const Calendar = () => {
  return (
    <div>
      <FullCalendar
        headerToolbar={{
          start: 'prev',
          center: 'title',
          end: 'next',
        }}
        plugins={[daygridPlugin]}
        height="568px"
      />
    </div>
  );
};
