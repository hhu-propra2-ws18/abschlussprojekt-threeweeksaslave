import React, {Component} from 'react';

export default class App extends Component {
    constructor(props) {
        super(props);

        this.state = {
            userName: ""
        }
    }

    componentDidMount() {

    }

    render() {

        return (
            <BrowserRouter>
                <Switch>

                    <Navbar
                        userName={this.state.userName}
                        handleLogout={this.doLogout}
                    />

                    <Row className = "main">
                        <Col s={12} m={10} l={8} offset={"s0 m1 l2"}>
                        </Col>
                    </Row>
                </Switch>
            </BrowserRouter>
        )
    }
}