import {useState} from "react";
import InfoItemDisplay from "../InfoGroupDisplay";
import TodoItemDisplay from "../TodoItemsDisplay";
import {Container, Nav, Navbar} from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.css"

export default function Dashboard() {

    const [activeSection, setActiveSection] = useState("InfoItemDisplay");
  return (
    <div>
        <Navbar expand="lg" className="bg-body-tertiary" fixed="top">
            <Container>
                <Navbar.Brand href="#home">Garnet</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav.Link className="mx-2" onClick={() => setActiveSection("InfoItemDisplay")}
                        active={activeSection === 'InfoItemDisplay'}>资料列表</Nav.Link>
                    <Nav.Link className="mx-2" onClick={() => setActiveSection("TodoItemDisplay")}
                        active={activeSection === 'TodoItemDisplay'}>待办事项</Nav.Link>
                </Navbar.Collapse>
            </Container>
        </Navbar>
        <Container style={{ marginTop: '20px' }}>
            {activeSection === 'InfoItemDisplay' && <InfoItemDisplay />}
            {activeSection === 'TodoItemDisplay' && <TodoItemDisplay />}
        </Container>
    </div>
  );
}
