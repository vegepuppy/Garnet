import InfoItemDisplay from "../InfoItemsDisplay";
import TodoItemDisplay from "../TodoItemsDisplay";

export default function Dashboard() {
  return (
    <>
      <InfoItemDisplay belong={1}/>
      <TodoItemDisplay />
    </>
  );
}
